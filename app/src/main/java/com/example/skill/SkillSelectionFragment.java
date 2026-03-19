package com.example.skill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class SkillSelectionFragment extends Fragment {
    private CheckBox checkboxJava, checkboxKotlin, checkboxAndroidSDK, checkboxFirebase;
    private CheckBox checkboxGit, checkboxHTML, checkboxCSS, checkboxJavaScript;
    private Button buttonStartTest;
    private TextView textViewWelcome;
    private SharedViewModel viewModel;

    @Nullable

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        initializeUIComponents(view);
        setupWelcomeMessage();

        buttonStartTest.setOnClickListener(v -> proceedToTest());
    }

    private void initializeUIComponents(View view) {
        buttonStartTest = view.findViewById(R.id.buttonStartTest);
        textViewWelcome = view.findViewById(R.id.textViewWelcome);
    }

    private void setupWelcomeMessage() {
        String name = viewModel.getUserName().getValue();
        if (name != null) {
            textViewWelcome.setText("Welcome, " + name + "!\nSelect your skills and start the test.");
        }
    }

    private void proceedToTest() {
        List<String> selectedSkills = getSelectedSkills();

        if (selectedSkills.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one skill", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save selected skills to ViewModel
        viewModel.setSelectedSkills(selectedSkills);

        // Reset test data
        viewModel.resetTestData();

        Toast.makeText(getContext(), "Starting test with " + selectedSkills.size() + " skills", Toast.LENGTH_SHORT).show();

        // Navigate to Test Fragment
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new TestFragment());
        }
    }

    private List<String> getSelectedSkills() {
        List<String> skills = new ArrayList<>();

        if (checkboxJava.isChecked()) skills.add("Java");
        if (checkboxKotlin.isChecked()) skills.add("Kotlin");
        if (checkboxAndroidSDK.isChecked()) skills.add("Android SDK");
        if (checkboxFirebase.isChecked()) skills.add("Firebase");
        if (checkboxGit.isChecked()) skills.add("Git");
        if (checkboxHTML.isChecked()) skills.add("HTML");
        if (checkboxCSS.isChecked()) skills.add("CSS");
        if (checkboxJavaScript.isChecked()) skills.add("JavaScript");

        return skills;
    }
}
