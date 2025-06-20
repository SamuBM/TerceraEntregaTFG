plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // ¡Aplica el plugin de Google Services que declaraste a nivel de proyecto!
    id("com.google.gms.google-services") // <--- Aquí aplicas el plugin
}

android {
    namespace = "com.example.tfg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tfg"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    val firebaseBoM = platform("com.google.firebase:firebase-bom:32.7.1") // <--- Aquí la BoM
    implementation(firebaseBoM)

    // Ahora, añade las bibliotecas de Firebase que necesites, SIN especificar la versión
    // porque la BoM se encarga de eso.
    // Basado en tu configuración, seguro querrás estas:
    implementation("com.google.firebase:firebase-analytics-ktx") // Para Google Analytics
    implementation("com.google.firebase:firebase-firestore-ktx") // Para Cloud Firestore

}