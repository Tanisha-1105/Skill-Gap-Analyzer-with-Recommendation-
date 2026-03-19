# Skill Gap Analyzer - Multi-Screen Android Application

## 📱 Project Overview

**Skill Gap Analyzer** is a comprehensive Android application that helps users assess their technical skills, take AI-generated tests, and receive personalized improvement suggestions. The app uses Google's Generative AI (Gemini) to create dynamic, skill-based questions.

---

## 🏗️ Architecture

### Single Activity with Multiple Fragments
- **MainActivity.java**: Fragment container and navigation manager
- **SharedViewModel.java**: Shared data between fragments using LiveData

### Fragments (5 Screens)
1. **LandingFragment** - Welcome/Home page
2. **LoginFragment** - User registration mockup
3. **ProfileFragment** - Skill selection
4. **TestFragment** - AI-generated multiple choice questions (15 questions)
5. **ResultsFragment** - Score, results, and AI-generated improvement suggestions

### Supporting Classes
- **Question.java** - Data class for quiz questions
- **SharedViewModel.java** - ViewModel for data sharing

---

## 🔄 App Flow

```
Landing Page 
    ↓ (Get Started)
Login Page (Enter Name & Email)
    ↓ (Login)
Profile Page (Select Skills)
    ↓ (Start Test)
Test Page (Answer 15 Questions)
    ↓ (Submit Test)
Results Page (View Score & Suggestions)
    ↓ (Retake / Home)
```

---

## 📋 Features

### 1. Landing Page
- Welcome message with app description
- "Get Started" button to begin

### 2. Login (Mockup)
- Name input field
- Email input field
- Basic validation
- User data stored in ViewModel

### 3. Profile Screen
- Welcome message (personalized with username)
- Multiple skill checkboxes:
  - Java, Kotlin, Android SDK, Firebase, Git
  - HTML, CSS, JavaScript
- "Start Test" button

### 4. Test Screen
- AI-generated 15 multiple-choice questions
- Skill-based question generation using Google Gemini AI
- Progress indicator (e.g., "5 / 15")
- Radio buttons for A/B/C/D options
- Next/Submit buttons
- Loading spinner during question generation

### 5. Results Screen
- Score percentage (e.g., "85%")
- Correct answers display (e.g., "13 / 15")
- AI-generated improvement suggestions
- "Retake Test" button
- "Home" button

---

## 🤖 AI Integration (Google Generative AI)

### Setup Instructions

#### Step 1: Get API Key
1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Click "Create API Key"
3. Copy the API key

#### Step 2: Add API Key to TestFragment & ResultsFragment
In both files, replace:
```java
private static final String API_KEY = "YOUR_GOOGLE_API_KEY";
```

With your actual API key:
```java
private static final String API_KEY = "sk-1234567890abcdef...";
```

### How AI Works
1. **Question Generation**: Uses Gemini API with a detailed prompt specifying:
   - Skill area based on selected skills
   - Exactly 15 multiple-choice questions
   - Structured format (Q1-Q15 with A/B/C/D options)
   - Correct answers

2. **Improvement Suggestions**: 
   - Analyzes test performance
   - Generates personalized recommendations
   - Suggests study resources
   - Identifies strength/weakness areas

### Fallback (Mock Data)
If API key is invalid or network error occurs, app loads mock questions for demo purposes.

---

## 📱 XML Layouts

| Fragment | Layout File |
|----------|------------|
| Landing | `fragment_landing.xml` |
| Login | `fragment_login.xml` |
| Profile | `fragment_profile.xml` |
| Test | `fragment_test.xml` |
| Results | `fragment_results.xml` |

**Main Activity**: `activity_main.xml` (FrameLayout container)

---

## 📦 Dependencies Added

```gradle
// Google Generative AI (Gemini)
implementation("com.google.ai.client.generativeai:google-generative-ai-android:0.1.1")

// Fragment support
implementation("androidx.fragment:fragment:1.6.2")
implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
```

---

## 🔐 Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 📊 Data Flow

### User Data (SharedViewModel)
- `userName`: User's name
- `userEmail`: User's email
- `selectedSkills`: List of selected skills
- `testQuestions`: List of Question objects
- `userAnswers`: List of selected options (0-3)
- `testScore`: Final percentage score
- `correctAnswers`: Number of correct answers

### Question Object Structure
```java
Question {
    String questionText
    String optionA, optionB, optionC, optionD
    int correctAnswer (0-3)
}
```

---

## 🎨 UI Components

### Reusable Drawables
- `result_background.xml`: Light gray rounded rectangle for result boxes
- `edit_text_background.xml`: Styled EditText background

---

## 🚀 How to Run

1. **Open project in Android Studio**
2. **Add your Google API key** to TestFragment.java and ResultsFragment.java
3. **Sync Gradle** (File → Sync Now)
4. **Run the app** on emulator or device

---

## 💡 Key Implementation Details

### Fragment Navigation
```java
((MainActivity) getActivity()).navigateToFragment(new LoginFragment());
```

### ViewModel Access
```java
viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
```

### AI Integration (Async)
```java
new Thread(() -> {
    var response = generativeModel.generateContent(prompt);
    getActivity().runOnUiThread(() -> {
        // Update UI
    });
}).start();
```

---

## 📝 Future Enhancements

- [ ] Save test history to database
- [ ] User authentication with Firebase
- [ ] Progress tracking over time
- [ ] Offline test capability
- [ ] Multiple language support
- [ ] Dark mode
- [ ] Certificates upon passing tests
- [ ] Social sharing features

---

## 🐛 Troubleshooting

### Issue: "Invalid API Key"
**Solution**: Verify your Google API key is correct and has access to Generative AI API.

### Issue: "No questions generated"
**Solution**: Check internet connection. App will load mock questions if API fails.

### Issue: Fragments not showing
**Solution**: Ensure `fragmentContainer` FrameLayout is defined in `activity_main.xml`.

---

## 📄 License

This project is open source and available for educational purposes.

---

## ✅ Checklist

- ✅ Single Activity with 5 Fragments
- ✅ SharedViewModel for data sharing
- ✅ Google Generative AI integration
- ✅ 15 multiple-choice questions
- ✅ Score calculation and display
- ✅ AI-generated improvement suggestions
- ✅ Responsive UI with ScrollView
- ✅ Error handling with fallback mock data
- ✅ Internet permission configured

---

**Happy Learning with Skill Gap Analyzer! 🎓**
