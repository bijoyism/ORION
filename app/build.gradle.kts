plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.bijoyism.orion"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bijoyism.orion"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.09.00")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.10.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")

implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
