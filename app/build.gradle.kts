plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.cmc.aqualis"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cmc.aqualis"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.android.material:material:1.11.0")

    implementation("com.airbnb.android:lottie:5.0.3")

    implementation ("androidx.room:room-runtime:2.4.2")
    implementation ("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    
    // MPAndroidChart pour les graphiques du tableau de bord
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}