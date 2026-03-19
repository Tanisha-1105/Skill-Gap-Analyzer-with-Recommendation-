package com.example.skill;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends Fragment {
    private TextView textViewQuestion, textViewProgress;
    private RadioGroup radioGroupOptions;
    private RadioButton radioOptionA, radioOptionB, radioOptionC, radioOptionD;
    private Button buttonNext, buttonSubmit;
    private ProgressBar progressBar;
    private SharedViewModel viewModel;
    private int currentQuestionIndex = 0;
    private int selectedOption = -1;

    // Google Generative AI API Key
    private static final String API_KEY = "AIzaSyD0Sx4BLuhAXrl4Thlpacua-hcLdmwig6o"; // IMPORTANT: Put your active API key here!
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        initializeUIComponents(view);
        generateTestQuestions();
    }

    private void initializeUIComponents(View view) {
        textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewProgress = view.findViewById(R.id.textViewProgress);
        radioGroupOptions = view.findViewById(R.id.radioGroupOptions);
        radioOptionA = view.findViewById(R.id.radioOptionA);
        radioOptionB = view.findViewById(R.id.radioOptionB);
        radioOptionC = view.findViewById(R.id.radioOptionC);
        radioOptionD = view.findViewById(R.id.radioOptionD);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        progressBar = view.findViewById(R.id.progressBar);

        radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOptionA) selectedOption = 0;
            else if (checkedId == R.id.radioOptionB) selectedOption = 1;
            else if (checkedId == R.id.radioOptionC) selectedOption = 2;
            else if (checkedId == R.id.radioOptionD) selectedOption = 3;
        });

        buttonNext.setOnClickListener(v -> nextQuestion());
        buttonSubmit.setOnClickListener(v -> submitTest());
    }

    private void generateTestQuestions() {
        List<String> selectedSkills = viewModel.getSelectedSkills().getValue();
        if (selectedSkills == null || selectedSkills.isEmpty()) {
            Toast.makeText(getContext(), "No skills selected", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonNext.setEnabled(false);

        String skillsString = String.join(", ", selectedSkills);
        String prompt = "Generate exactly 10 multiple choice questions to test knowledge in these skills: " + skillsString +
                "\n\nCRITICAL INSTRUCTIONS:\n" +
                "1. DO NOT use any markdown formatting (no asterisks, no bold text).\n" +
                "2. If a question requires a code snippet, put the code directly below the question text.\n" +
                "3. For EACH question, provide EXACTLY in this format:\n" +
                "Q[number]: [Question Text]\n" +
                "[Code snippet here if applicable]\n" +
                "A) [Option A]\n" +
                "B) [Option B]\n" +
                "C) [Option C]\n" +
                "D) [Option D]\n" +
                "Answer: [A/B/C/D]\n\n" +
                "Generate all 10 questions now.";

        new Thread(() -> {
            try {
                List<Question> questions = callGeminiAPI(prompt);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (questions.size() >= 10) {
                            viewModel.setTestQuestions(new ArrayList<>(questions.subList(0, 10)));
                            displayCurrentQuestion();
                            progressBar.setVisibility(View.GONE);
                            buttonNext.setEnabled(true);
                        } else if (!questions.isEmpty()) {
                            viewModel.setTestQuestions(questions);
                            displayCurrentQuestion();
                            progressBar.setVisibility(View.GONE);
                            buttonNext.setEnabled(true);
                            Toast.makeText(getContext(), "Generated " + questions.size() + " questions",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed to generate questions. Please check your API and try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        String errorMsg = e.getMessage() != null ? e.getMessage() : "API Error";
                        Toast.makeText(getContext(), "API Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    });
                }
            }
        }).start();
    }

    private List<Question> callGeminiAPI(String prompt) throws Exception {
        if (API_KEY.isEmpty() || API_KEY.equals("YOUR_API_KEY_HERE")) {
            throw new Exception("API Key not set");
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

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

        // Increased token limit so questions don't get cut off!
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.9);
        generationConfig.addProperty("topP", 0.95);
        generationConfig.addProperty("maxOutputTokens", 6144);
        jsonRequest.add("generationConfig", generationConfig);

        JsonArray safetySettings = new JsonArray();
        jsonRequest.add("safetySettings", safetySettings);

        RequestBody body = RequestBody.create(
                jsonRequest.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "No response body";

            if (!response.isSuccessful()) {
                String errorMsg = "HTTP " + response.code() + " - " + responseBody;
                try {
                    JsonObject errorObj = JsonParser.parseString(responseBody).getAsJsonObject();
                    if (errorObj.has("error")) {
                        errorMsg = errorObj.getAsJsonObject("error").get("message").getAsString();
                    }
                } catch (Exception e) {
                    errorMsg = responseBody;
                }
                throw new Exception(errorMsg);
            }

            if (responseBody.isEmpty()) {
                throw new Exception("Empty response from API");
            }

            return parseQuestionsFromResponse(responseBody);
        }
    }

    private List<Question> parseQuestionsFromResponse(String response) {
        List<Question> questions = new ArrayList<>();

        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

            if (candidates != null && !candidates.isEmpty()) {
                JsonObject candidate = candidates.get(0).getAsJsonObject();
                JsonObject content = candidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");

                if (parts != null && !parts.isEmpty()) {
                    String text = parts.get(0).getAsJsonObject().get("text").getAsString();
                    String[] questionBlocks = text.split("(?=\\*?\\*?Q\\d+:)");

                    for (String block : questionBlocks) {
                        if (block.trim().isEmpty()) continue;

                        try {
                            Question question = parseQuestionBlock(block.trim());
                            if (question != null) {
                                questions.add(question);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    private Question parseQuestionBlock(String block) {
        try {
            int idxA = block.indexOf("A)");
            int idxB = block.indexOf("B)");
            int idxC = block.indexOf("C)");
            int idxD = block.indexOf("D)");
            int idxAns = block.indexOf("Answer:");

            if (idxA == -1 || idxB == -1 || idxC == -1 || idxD == -1 || idxAns == -1) {
                return null;
            }

            String questionText = block.substring(0, idxA).replaceAll("^\\*?\\*?Q\\d+:\\*?\\*?\\s*", "").trim();

            String optionA = block.substring(idxA + 2, idxB).trim();
            String optionB = block.substring(idxB + 2, idxC).trim();
            String optionC = block.substring(idxC + 2, idxD).trim();
            String optionD = block.substring(idxD + 2, idxAns).trim();

            String answerLine = block.substring(idxAns).trim();
            int correctAnswer = -1;

            answerLine = answerLine.replace("*", "");

            if (answerLine.contains("A)") || answerLine.endsWith("A")) correctAnswer = 0;
            else if (answerLine.contains("B)") || answerLine.endsWith("B")) correctAnswer = 1;
            else if (answerLine.contains("C)") || answerLine.endsWith("C")) correctAnswer = 2;
            else if (answerLine.contains("D)") || answerLine.endsWith("D")) correctAnswer = 3;

            if (questionText.isEmpty() || correctAnswer == -1) return null;

            optionA = optionA.replace("**", "");
            optionB = optionB.replace("**", "");
            optionC = optionC.replace("**", "");
            optionD = optionD.replace("**", "");

            return new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer);

        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayCurrentQuestion() {
        List<Question> questions = viewModel.getTestQuestions().getValue();
        if (questions == null || questions.isEmpty()) {
            Toast.makeText(getContext(), "No questions available", Toast.LENGTH_SHORT).show();
            buttonNext.setEnabled(false);
            return;
        }

        currentQuestionIndex = viewModel.getCurrentQuestionIndex().getValue() != null ?
                viewModel.getCurrentQuestionIndex().getValue() : 0;

        if (currentQuestionIndex >= questions.size()) {
            currentQuestionIndex = 0;
            viewModel.setCurrentQuestionIndex(0);
        }

        Question question = questions.get(currentQuestionIndex);

        textViewQuestion.setText(question.getQuestionText());
        radioOptionA.setText("A) " + question.getOptionA());
        radioOptionB.setText("B) " + question.getOptionB());
        radioOptionC.setText("C) " + question.getOptionC());
        radioOptionD.setText("D) " + question.getOptionD());

        radioGroupOptions.clearCheck();
        selectedOption = -1;

        textViewProgress.setText((currentQuestionIndex + 1) + " / " + questions.size());

        buttonNext.setEnabled(true);
        buttonSubmit.setEnabled(true);

        if (currentQuestionIndex == questions.size() - 1) {
            buttonNext.setVisibility(View.GONE);
            buttonSubmit.setVisibility(View.VISIBLE);
        } else {
            buttonNext.setVisibility(View.VISIBLE);
            buttonSubmit.setVisibility(View.GONE);
        }
    }

    private void nextQuestion() {
        if (selectedOption == -1) {
            Toast.makeText(getContext(), "Please select an option", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Question> questions = viewModel.getTestQuestions().getValue();
        if (questions == null) return;

        viewModel.addUserAnswer(selectedOption);
        currentQuestionIndex++;

        if (currentQuestionIndex < questions.size()) {
            viewModel.setCurrentQuestionIndex(currentQuestionIndex);
            displayCurrentQuestion();
        } else {
            submitTest();
        }
    }

    private void submitTest() {
        if (selectedOption == -1) {
            Toast.makeText(getContext(), "Please select an option for the last question", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.addUserAnswer(selectedOption);
        calculateResults();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new ResultsFragment());
        }
    }

    private void calculateResults() {
        List<Question> questions = viewModel.getTestQuestions().getValue();
        List<Integer> userAnswers = viewModel.getUserAnswers().getValue();

        if (questions == null || userAnswers == null) return;

        int correctCount = 0;
        for (int i = 0; i < questions.size() && i < userAnswers.size(); i++) {
            if (questions.get(i).getCorrectAnswer() == userAnswers.get(i)) {
                correctCount++;
            }
        }

        int totalQuestions = questions.size();
        int score = totalQuestions > 0 ? (correctCount * 100) / totalQuestions : 0;
        viewModel.setTestScore(score);
        viewModel.setCorrectAnswers(correctCount);
    }
}