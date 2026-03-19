package com.example.skill;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResultsFragment extends Fragment {
    private static final String TAG = "ResultsFragment";
    private TextView textViewScore, textViewCorrectAnswers, textViewImprovements;

    private ProgressBar progressBar;
    private SharedViewModel viewModel;
    private static final String API_KEY = ""; // Google API key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        initializeUIComponents(view);
        displayResults();
        generateImprovements();
    }

    private void initializeUIComponents(View view) {
        textViewScore = view.findViewById(R.id.textViewScore);
        textViewCorrectAnswers = view.findViewById(R.id.textViewCorrectAnswers);
        textViewImprovements = view.findViewById(R.id.textViewImprovements);
        Button buttonRetake = view.findViewById(R.id.buttonRetake);
        Button buttonHome = view.findViewById(R.id.buttonHome);
        Button buttonDownloadPdf = view.findViewById(R.id.buttonHistory);
        progressBar = view.findViewById(R.id.progressBar);
        buttonDownloadPdf.setOnClickListener(v -> {
            if (checkPermission()) {
                generatePDF();
            } else {
                requestPermission();
            }
        });
        buttonRetake.setOnClickListener(v -> retakeTest());
        buttonHome.setOnClickListener(v -> goHome());
    }

    private void displayResults() {
        Integer score = viewModel.getTestScore().getValue();
        Integer correctAnswers = viewModel.getCorrectAnswers().getValue();

        if (score != null) {
            textViewScore.setText("Score: " + score + "%");
        }

        if (correctAnswers != null) {
            textViewCorrectAnswers.setText("Correct Answers: " + correctAnswers + " out of 10");
        }
    }

    private void generateImprovements() {
        List<String> selectedSkills = viewModel.getSelectedSkills().getValue();
        Integer correctAnswers = viewModel.getCorrectAnswers().getValue();

        if (selectedSkills == null || correctAnswers == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String skillsString = String.join(", ", selectedSkills);
        String prompt = "The user tested their knowledge on these skills: " + skillsString +
                       "\nThey got " + correctAnswers + " out of 10 questions correct (Score: " + (correctAnswers * 100 / 10) + "%).\n\n" +
                       "Based on this performance, provide:\n" +
                       "1. Areas where they performed well\n" +

                       "2. Areas needing improvement\n" +
                       "3. 3 specific recommendations to improve\n" +
                       "4. Resources they should study\n\n" +
                       "Keep the response concise and actionable (max 300 words).";

        new Thread(() -> {
            try {
                Log.d(TAG, "Generating AI improvements for: " + skillsString);
                String improvements = callGeminiAPI(prompt);
                Log.d(TAG, "Got improvements: " + improvements.substring(0, Math.min(100, improvements.length())) + "...");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        textViewImprovements.setText(improvements);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "AI Analysis Complete", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "API Error in generateImprovements: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        displayMockImprovements();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Recommendations Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        }).start();
    }

    private String callGeminiAPI(String prompt) throws Exception {
        Log.d(TAG, "callGeminiAPI started for improvements");

        // Validate API key first
        if (API_KEY.isEmpty()) {
            throw new Exception("API Key not set");
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // Create request using Google Generative AI format
        JsonObject jsonRequest = new JsonObject();
        JsonArray contentsArray = new JsonArray();
        JsonObject contentObj = new JsonObject();
        JsonArray partsArray = new JsonArray();
        JsonObject partObj = new JsonObject();
        partObj.addProperty("text", prompt);
        partsArray.add(partObj);
        contentObj.add("parts", partsArray);
        contentsArray.add(contentObj);
        jsonRequest.add("contents", contentsArray);

        // Safety settings
        JsonArray safetySettings = new JsonArray();
        jsonRequest.add("safetySettings", safetySettings);

        String jsonPayload = jsonRequest.toString();
        Log.d(TAG, "Payload length: " + jsonPayload.length());

        RequestBody body = RequestBody.create(
                jsonPayload,
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "Response code: " + response.code());

            String responseBody = response.body() != null ? response.body().string() : "No response";

            if (!response.isSuccessful()) {
                String errorMsg = "HTTP " + response.code();
                try {
                    JsonObject errorObj = JsonParser.parseString(responseBody).getAsJsonObject();
                    if (errorObj.has("error")) {
                        errorMsg = errorObj.getAsJsonObject("error").get("message").getAsString();
                    }
                } catch (Exception e) {
                    errorMsg += ": " + responseBody;
                }

                Log.e(TAG, "API Error: " + errorMsg);
                throw new Exception(errorMsg);
            }

            String improvements = parseImprovementsFromResponse(responseBody);
            Log.d(TAG, "Successfully parsed improvements");
            return improvements;
        } catch (Exception e) {
            Log.e(TAG, "Exception during API call: " + e.getMessage());
            throw e;
        }
    }
    private void generatePDF() {
        List<String> skills = viewModel.getSelectedSkills().getValue();
        List<Question> questions = viewModel.getTestQuestions().getValue();
        List<Integer> userAnswers = viewModel.getUserAnswers().getValue();

        if (skills == null || skills.isEmpty() || questions == null || userAnswers == null) return;

        String skillName = skills.get(0);
        String score = textViewScore.getText().toString();
        String feedback = textViewImprovements.getText().toString();

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        int x = 50, y = 60;

        // Header Title
        paint.setFakeBoldText(true);
        paint.setTextSize(26f);
        paint.setColor(android.graphics.Color.parseColor("#1565C0"));
        canvas.drawText("Skill Analysis Report", x, y, paint);

        y += 15;
        paint.setStrokeWidth(2f);
        canvas.drawLine(x, y, 545, y, paint);

        y += 40;
        paint.setFakeBoldText(false);
        paint.setTextSize(12f);
        paint.setColor(android.graphics.Color.DKGRAY);
        canvas.drawText("Generated on: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date()), x, y, paint);

        y += 25;
        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(16f);
        paint.setFakeBoldText(true);
        canvas.drawText("Competency: " + skillName.toUpperCase(), x, y, paint);

        // Score Box
        y += 40;
        Paint boxPaint = new Paint();
        boxPaint.setColor(android.graphics.Color.parseColor("#E3F2FD"));
        canvas.drawRect(x, y - 20, 300, y + 30, boxPaint);

        paint.setTextSize(18f);
        paint.setColor(android.graphics.Color.parseColor("#0D47A1"));
        canvas.drawText("Final Result: " + score, x + 15, y + 15, paint);

        // --- NEW SECTION: QUESTIONS & ANSWERS ---
        y += 80;
        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(18f);
        paint.setFakeBoldText(true);
        canvas.drawText("Question Review:", x, y, paint);
        y += 30;

        paint.setTextSize(11f);
        paint.setFakeBoldText(false);

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int userSelection = userAnswers.get(i);
            int correctIdx = q.getCorrectAnswer();
            boolean isCorrect = (userSelection == q.getCorrectAnswer());

            // Draw Question Number and Text (Truncated if too long)
            String qText = (i + 1) + ". " + q.getQuestionText();
            if (qText.length() > 85) qText = qText.substring(0, 82) + "...";
            canvas.drawText(qText, x, y, paint);
            y += 18;

            // Draw User Result
            paint.setFakeBoldText(true);
            if (isCorrect) {
                paint.setColor(android.graphics.Color.parseColor("#2E7D32")); // Green
                canvas.drawText("   ✓ Correct", x, y, paint);
            } else {
                paint.setColor(android.graphics.Color.parseColor("#C62828")); // Red
                String correctValue = "";
                if (correctIdx == 0) correctValue = q.getOptionA();
                else if (correctIdx == 1) correctValue = q.getOptionB();
                else if (correctIdx == 2) correctValue = q.getOptionC();
                else if (correctIdx == 3) correctValue = q.getOptionD();

                canvas.drawText("   ✗ Incorrect (Correct Answer: " + correctValue + ")", x, y, paint);
            }

            paint.setFakeBoldText(false);
            paint.setColor(android.graphics.Color.BLACK);
            y += 25;

            // Create a second page if questions overflow
            if (y > 780) {
                document.finishPage(page);
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }

        // --- AI RECOMMENDATIONS SECTION ---
        if (y > 600) { // New page for feedback if room is tight
            document.finishPage(page);
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            y = 50;
        }

        y += 40;
        paint.setTextSize(18f);
        paint.setFakeBoldText(true);
        paint.setColor(android.graphics.Color.BLACK);
        canvas.drawText("AI Recommendations:", x, y, paint);
        y += 30;

        paint.setTextSize(11f);
        paint.setFakeBoldText(false);
        paint.setLetterSpacing(0.01f);

        String[] paragraphsArray = feedback.split("\n");
        int maxWidth = 500;

        for (String singleParagraph : paragraphsArray) {
            if (singleParagraph.trim().isEmpty()) {
                y+=10;
                continue;
            }
            String[] wordsInLine = singleParagraph.split(" ");
            StringBuilder Currentline = new StringBuilder();

            for (String word : wordsInLine) {
                if (paint.measureText(Currentline + " " + word) < maxWidth) {
                    Currentline.append(word).append(" ");
                } else {
                    canvas.drawText(Currentline.toString(), x, y, paint);
                    y += 18;
                    Currentline = new StringBuilder(word).append(" ");
                    if (y > 800) {
                        document.finishPage(page);
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();
                        y = 50;
                        paint.setTextSize(11f);
                    }
                }
            }
            canvas.drawText(Currentline.toString(), x, y, paint);
            y += 22;
        }
        document.finishPage(page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Detailed_Report_" + skillName + ".pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "Full Report Downloaded!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        document.close();
    }
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }
    private String parseImprovementsFromResponse(String response) {
        try {
            Log.d(TAG, "Parsing improvements response...");
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

            if (candidates == null || candidates.isEmpty()) {
                Log.e(TAG, "No candidates in response");
                return "Unable to generate analysis.";
            }

            JsonObject candidate = candidates.get(0).getAsJsonObject();
            JsonObject content = candidate.getAsJsonObject("content");

            if (content == null) {
                Log.e(TAG, "No content in candidate");
                return "Unable to generate analysis.";
            }

            JsonArray parts = content.getAsJsonArray("parts");

            if (parts == null || parts.isEmpty()) {
                Log.e(TAG, "No parts in content");
                return "Unable to generate analysis.";
            }

            String result = parts.get(0).getAsJsonObject().get("text").getAsString();
            Log.d(TAG, "Successfully extracted improvements text");
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing improvements: " + e.getMessage(), e);
        }

        return "Unable to generate improvements at this time.";
    }

    private void displayMockImprovements() {
        Integer correctAnswers = viewModel.getCorrectAnswers().getValue();

        StringBuilder improvements = new StringBuilder();
        improvements.append("Performance Analysis:\n\n");
        improvements.append("Strengths:\n");
        improvements.append("- You demonstrated understanding of basic concepts\n\n");

        improvements.append("Areas for Improvement:\n");
        if (correctAnswers != null && correctAnswers < 8) {
            improvements.append("- Focus on core fundamentals\n");
            improvements.append("- Practice coding exercises regularly\n");
        } else {
            improvements.append("- Explore advanced topics\n");
        }

        improvements.append("\nRecommendations:\n");
        improvements.append("1. Practice coding problems daily\n");
        improvements.append("2. Review documentation for selected topics\n");
        improvements.append("3. Build projects to apply knowledge\n\n");

        improvements.append("Suggested Resources:\n");
        improvements.append("- Official documentation\n");
        improvements.append("- Online coding platforms\n");
        improvements.append("- Technical blogs and tutorials");

        textViewImprovements.setText(improvements.toString());
    }

    private void retakeTest() {
        viewModel.resetTestData();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new SkillInputFragment());
        }
    }

    private void goHome() {
        viewModel.resetTestData();
        viewModel.getUserName().setValue(null);
        viewModel.getUserEmail().setValue(null);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new ProfileFormFragment());
        }
    }
}
