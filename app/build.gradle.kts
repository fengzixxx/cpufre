plugins {
    id("com.android.application")
}

android {
    lint {
        baseline = file("lint-baseline.xml")
    }
    namespace = "com.example.app5"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app5"
        minSdk = 25
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}



dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.1079374315:GSLS_Tool:v1.4.5.2")
    annotationProcessor ("com.github.1079374315:GSLS_Tool:v1.4.5.2")
    implementation ("org.apache.commons:commons-io:1.3.2")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.0.3")
    implementation ("com.airbnb.android:lottie:3.7.0")


}