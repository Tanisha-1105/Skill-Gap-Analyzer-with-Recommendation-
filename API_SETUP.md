# Skill Gap Analyzer - API Setup Guide

## To Enable AI-Generated Questions:

1. **Get your Google API Key:**
   - Go to: https://aistudio.google.com/
   - Click "Create API Key"
   - Copy your API key

2. **Add to TestFragment.java:**
   - Open: `app/src/main/java/com/example/skill/TestFragment.java`
   - Find line with: `private static final String API_KEY = "AIzaSyDummy_Replace_With_Your_Actual_Key";`
   - Replace with: `private static final String API_KEY = "YOUR_ACTUAL_KEY_HERE";`

3. **Test:**
   - Run the app
   - Login with: tanishamhatre1105@gmail.com / 123456
   - Select skills
   - Click "Start Test" → Questions will be AI-generated

## Current Features:

✅ Built-in Login (tanishamhatre1105@gmail.com / 123456)
✅ Skill Selection
✅ Question Navigation (Fixed - Now properly changes between questions)
✅ Multiple Choice Test
✅ Score Calculation
✅ Results Display
✅ Demo Questions as Fallback

## If API Key is Not Set:
- App will automatically load 15 demo questions (no internet needed)
- You can still take the test and see your score

---

For more help, visit: https://ai.google.dev/
