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

public class ProfileFormFragment extends Fragment {
    private EditText editTextFirstName, editTextLastName, editTextEducation, editTextExperience;
    private Button buttonContinue;
    private TextView textViewEmail;
    private SharedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextEducation = view.findViewById(R.id.editTextEducation);
        editTextExperience = view.findViewById(R.id.editTextExperience);
        buttonContinue = view.findViewById(R.id.buttonContinue);
        textViewEmail = view.findViewById(R.id.textViewEmail);

        // Get current user data
        User currentUser = UserManager.getCurrentUser();
        if (currentUser != null) {
            textViewEmail.setText("Email: " + currentUser.getEmail());
            editTextFirstName.setText(currentUser.getFirstName());
            editTextLastName.setText(currentUser.getLastName());
            editTextEducation.setText(currentUser.getEducationLevel());
            editTextExperience.setText(currentUser.getExperience());
        }

        buttonContinue.setOnClickListener(v -> saveProfileAndContinue());
    }

    private void saveProfileAndContinue() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String education = editTextEducation.getText().toString().trim();
        String experience = editTextExperience.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || education.isEmpty() || experience.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to UserManager
        UserManager.updateCurrentUserProfile(firstName, lastName, education, experience);

        // Save to ViewModel
        viewModel.setUserName(firstName + " " + lastName);
        User user = UserManager.getCurrentUser();
        if (user != null) {
            viewModel.setUserEmail(user.getEmail());
        }

        // Navigate to Skill Input Fragment
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new SkillInputFragment());
        }
    }
}
