plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.skill"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.skill"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // OkHttp for HTTP requests to Google Generative AI
    //noinspection UseTomlInstead
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Fragment support
    //noinspection UseTomlInstead,GradleDependency
    implementation("androidx.fragment:fragment:1.6.2")
    //noinspection UseTomlInstead,GradleDependency
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}