package com.example.skill;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    // User Profile Data
    private MutableLiveData<String> userName = new MutableLiveData<>();
    private MutableLiveData<String> userEmail = new MutableLiveData<>();
    private MutableLiveData<List<String>> selectedSkills = new MutableLiveData<>(new ArrayList<>());

    // Test Data
    private MutableLiveData<List<Question>> testQuestions = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    private MutableLiveData<List<Integer>> userAnswers = new MutableLiveData<>(new ArrayList<>());

    // Results Data
    private MutableLiveData<Integer> testScore = new MutableLiveData<>(0);
    private MutableLiveData<String> improvements = new MutableLiveData<>("");
    private MutableLiveData<Integer> correctAnswers = new MutableLiveData<>(0);

    // Getters and Setters
    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        userName.setValue(name);
    }

    public MutableLiveData<String> getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        userEmail.setValue(email);
    }

    public MutableLiveData<List<String>> getSelectedSkills() {
        return selectedSkills;
    }

    public void setSelectedSkills(List<String> skills) {
        selectedSkills.setValue(new ArrayList<>(skills));
    }

    public MutableLiveData<List<Question>> getTestQuestions() {
        return testQuestions;
    }

    public void setTestQuestions(List<Question> questions) {
        testQuestions.setValue(questions);
    }

    public MutableLiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int index) {
        currentQuestionIndex.setValue(index);
    }

    public MutableLiveData<List<Integer>> getUserAnswers() {
        return userAnswers;
    }

    public void addUserAnswer(int answer) {
        List<Integer> answers = userAnswers.getValue();
        if (answers != null) {
            answers.add(answer);
            // Create new list to trigger LiveData update
            userAnswers.setValue(new ArrayList<>(answers));
        }
    }

    public MutableLiveData<Integer> getTestScore() {
        return testScore;
    }

    public void setTestScore(int score) {
        testScore.setValue(score);
    }

    public MutableLiveData<String> getImprovements() {
        return improvements;
    }

    public void setImprovements(String text) {
        improvements.setValue(text);
    }

    public MutableLiveData<Integer> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int count) {
        correctAnswers.setValue(count);
    }

    public void resetTestData() {
        currentQuestionIndex.setValue(0);
        userAnswers.setValue(new ArrayList<>());
    }
}
