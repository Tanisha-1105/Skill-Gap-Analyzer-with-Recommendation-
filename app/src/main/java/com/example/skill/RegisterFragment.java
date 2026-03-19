package com.example.skill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private EditText editTextEducation, editTextExperience;
    private Button buttonRegister, buttonBackToLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextEducation = view.findViewById(R.id.editTextEducation);
        editTextExperience = view.findViewById(R.id.editTextExperience);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        buttonBackToLogin = view.findViewById(R.id.buttonBackToLogin);

        buttonRegister.setOnClickListener(v -> performRegister());
        buttonBackToLogin.setOnClickListener(v -> goBackToLogin());
    }

    private void performRegister() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String education = editTextEducation.getText().toString().trim();
        String experience = editTextExperience.getText().toString().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            password.isEmpty() || education.isEmpty() || experience.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(getContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try to register
        if (UserManager.register(email, password, firstName, lastName, education, experience)) {
            Toast.makeText(getContext(), "Registration Successful! Please login.", Toast.LENGTH_SHORT).show();
            goBackToLogin();
        } else {
            Toast.makeText(getContext(), "Email already registered. Try another email.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBackToLogin() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new LoginFragment());
        }
    }
}
