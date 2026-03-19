package com.example.skill;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static List<User> users = new ArrayList<>();
    private static User currentUser = null;

    static {
        // Add some demo users
        users.add(new User("tanishamhatre1105@gmail.com", "123456", "Tanisha", "Mhatre", "Bachelor's", "Beginner"));
        users.add(new User("demo@gmail.com", "password", "Demo", "User", "Master's", "Intermediate"));
    }

    /**
     * Register a new user
     */
    public static boolean register(String email, String password, String firstName, String lastName,
                                   String educationLevel, String experience) {
        // Check if user already exists
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return false; // User already exists
            }
        }

        // Create and add new user
        User newUser = new User(email, password, firstName, lastName, educationLevel, experience);
        users.add(newUser);
        currentUser = newUser;
        return true;
    }

    /**
     * Login user
     */
    public static boolean login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    /**
     * Get current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Logout current user
     */
    public static void logout() {
        currentUser = null;
    }

    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Update current user profile
     */
    public static void updateCurrentUserProfile(String firstName, String lastName,
                                               String educationLevel, String experience) {
        if (currentUser != null) {
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEducationLevel(educationLevel);
            currentUser.setExperience(experience);
        }
    }
}
