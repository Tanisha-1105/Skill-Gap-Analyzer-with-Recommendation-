package com.example.skill;

public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String educationLevel;
    private String experience;

    public User(String email, String password, String firstName, String lastName, 
                String educationLevel, String experience) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.educationLevel = educationLevel;
        this.experience = experience;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
