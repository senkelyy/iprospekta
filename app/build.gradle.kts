import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Signature release optionnelle : lue depuis keystore.properties (local, hors depot)
// ou depuis les variables d'environnement de la CI. Si absente, le build release
// reste possible (non signe) et le build debug fonctionne toujours.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) load(keystorePropsFile.inputStream())
}
fun prop(name: String): String? =
    keystoreProps.getProperty(name) ?: System.getenv(name)

val hasReleaseKeystore =
    prop("KEYSTORE_FILE") != null && prop("KEYSTORE_PASSWORD") != null

android {
    namespace = "com.andersen.iprospekta"
    // compileSdk 35 (Android 15) : combo stable avec AGP 8.7.3.
    // Pour viser officiellement Android 16 (API 36, requis par Google Play),
    // passer AGP a 8.9+, Gradle a 8.11+, puis compileSdk/targetSdk = 36.
    compileSdk = 35

    defaultConfig {
        applicationId = "com.andersen.iprospekta"
        minSdk = 29          // Android 10
        targetSdk = 35       // Android 15 (voir note ci-dessus pour 36)
        versionCode = 1
        versionName = "0.1.0"
        vectorDrawables { useSupportLibrary = true }
    }

    if (hasReleaseKeystore) {
        signingConfigs {
            create("release") {
                storeFile = file(prop("KEYSTORE_FILE")!!)
                storePassword = prop("KEYSTORE_PASSWORD")
                keyAlias = prop("KEY_ALIAS")
                keyPassword = prop("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Carte OpenStreetMap + hors ligne (publie sur Maven Central)
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
