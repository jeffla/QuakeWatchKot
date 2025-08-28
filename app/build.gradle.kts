plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"
}

android {
    namespace = "com.example.quakewatch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quakewatch"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        val mapsKey: String = providers.gradleProperty("MAPS_API_KEY").orNull ?: ""
        resValue("string", "google_maps_key", mapsKey)
        val contactEmail: String = providers.gradleProperty("CONTACT_EMAIL").orNull ?: ""
        buildConfigField("String", "CONTACT_EMAIL", "\"$contactEmail\"")
    }

    // Make Java + Kotlin both target 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
        jvmToolchain(17)
    }

    // Turn on Compose
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions { kotlinCompilerExtensionVersion = "1.7.3" }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

}

dependencies {
    // AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.foundation:foundation")
    implementation("com.google.maps.android:maps-compose:6.7.2")

    // Compose (use BOM to keep versions aligned)
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Hilt (kapt path to avoid KSP issues)
    implementation("com.google.dagger:hilt-android:2.57.1")
    kapt("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")

    // Moshi (no codegen, safe with kapt-less setup)
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.squareup.moshi:moshi:1.15.1")

}
