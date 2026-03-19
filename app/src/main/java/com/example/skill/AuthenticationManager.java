package com.example.skill;

public class AuthenticationManager {
    // Built-in credentials
    private static final String VALID_EMAIL = "demo@gmail.com";
    private static final String VALID_PASSWORD = "123456";

    /**
     * Validate login credentials
     * @param email User email
     * @param password User password
     * @return true if credentials are valid
     */
    public static boolean validateLogin(String email, String password) {
        return email != null && password != null &&
               email.equals(VALID_EMAIL) &&
               password.equals(VALID_PASSWORD);
    }

    /**
     * Get demo user name
     */
    public static String getDemoUserName() {
        return "Tanisha Mhatre";
    }
}
