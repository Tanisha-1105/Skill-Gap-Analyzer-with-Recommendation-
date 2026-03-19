package com.example.skill;

public class Question {
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctAnswer; // 0 for A, 1 for B, 2 for C, 3 for D

    public Question(String questionText, String optionA, String optionB, 
                   String optionC, String optionD, int correctAnswer) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    // Getters
    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCorrectOptionText() {
        switch (correctAnswer) {
            case 0:
                return optionA;
            case 1:
                return optionB;
            case 2:
                return optionC;
            case 3:
                return optionD;
            default:
                return "";
        }
    }
}
