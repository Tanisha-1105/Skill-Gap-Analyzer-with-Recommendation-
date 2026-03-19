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

public class LoginFragment extends Fragment {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SharedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        textViewRegister = view.findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(v -> performLogin());
        
        if (textViewRegister != null) {
            textViewRegister.setOnClickListener(v -> goToRegister());
        }
    }

    private void performLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try login with UserManager
        if (UserManager.login(email, password)) {
            User user = UserManager.getCurrentUser();
            viewModel.setUserEmail(user.getEmail());

            Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();

            // Navigate to Profile Form
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToFragment(new ProfileFormFragment());
            }
        } else {
            Toast.makeText(getContext(), "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToRegister() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToFragment(new RegisterFragment());
        }
    }
}
