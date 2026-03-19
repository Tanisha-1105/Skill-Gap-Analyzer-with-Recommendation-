package com.example.skill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillInputFragment extends Fragment {
    private EditText editTextSkills;
    private Button buttonStartTest;
    private TextView textViewWelcome, textViewInstructions;
    private SharedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skill_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        editTextSkills = view.findViewById(R.id.editTextSkills);
        buttonStartTest = view.findViewById(R.id.buttonStartTest);
        textViewWelcome = view.findViewById(R.id.textViewWelcome);
        textViewInstructions = view.findViewById(R.id.textViewInstructions);

        setupWelcomeMessage();
        buttonStartTest.setOnClickListener(v -> proceedToTest());
    }

    private void setupWelcomeMessage() {
        String name = viewModel.getUserName().getValue();
        if (name != null) {
            textViewWelcome.setText("Welcome, " + name + "!");
        }
    }

    private void proceedToTest() {
        String skillsInput = editTextSkills.getText().toString().trim();

        if (skillsInput.isEmpty()) {
            Toast.makeText(getContext(), "Please enter at least one skill", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert comma-separated skills to list
        List<String> selectedSkills = parseSkills(skillsInput);

        if (selectedSkills.isEmpty()) {
            Toast.makeText(getContext(), "Please enter valid skills", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save selected skills to ViewModel
        viewModel.setSelectedSkills(selectedSkills);

        // Reset test data
        viewModel.resetTestData();

        String skillsDisplay = String.join(", ", selectedSkills);
        Toast.makeText(getContext(), "Starting test for: " + skillsDisplay, Toast.LENGTH_SHORT).show();

        // Navigate to Test Fragment
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new TestFragment());
        }
    }

    private List<String> parseSkills(String input) {
        List<String> skills = new ArrayList<>();
        
        // Split by comma
        String[] skillArray = input.split(",");
        
        for (String skill : skillArray) {
            String trimmedSkill = skill.trim();
            if (!trimmedSkill.isEmpty()) {
                skills.add(trimmedSkill);
            }
        }
        
        return skills;
    }
}
