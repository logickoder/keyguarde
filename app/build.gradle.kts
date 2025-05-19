import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.performance)
    alias(libs.plugins.gms)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val keystoreProperties = Properties()
rootProject.file("local.properties").let { file ->
    if (file.exists()) {
        keystoreProperties.load(file.inputStream())
    }
}

/**
 * Get the value of a key from the keystore properties or from the environment variables.
 * @param key The key to look for.
 * @param properties The properties file to look in.
 * @return The value of the key, or null if not found.
 */
fun getValue(key: String): String {
    return (keystoreProperties[key] ?: System.getenv(key))?.toString().orEmpty()
}

android {
    namespace = "dev.logickoder.keyguarde"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.logickoder.keyguarde"
        minSdk = 26
        targetSdk = 36
        versionCode = 8
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        create("release") {
            getValue("KEYSTORE_PATH").let {
                if (it.isNotBlank()) {
                    storeFile = file(it)
                }
            }
            storePassword = getValue("KEYSTORE_PASSWORD")
            keyAlias = getValue("KEY_ALIAS")
            keyPassword = getValue("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".dev"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.firebase.analytics)
    coreLibraryDesugaring(libs.desugarjdklibs)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.fonts)
    implementation(libs.androidx.icons)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coil
    implementation(libs.coil)

    // Datastore
    implementation(libs.datastore)

    // Firebase
    implementation(platform(libs.firebase))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.performance)

    // Junit
    testImplementation(libs.junit)

    // Kotlin
    implementation(libs.kotlin.immutable)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.serialization)

    // Napier
    implementation(libs.napier)

    // Play Services
    implementation(libs.play.services.ads)

    // Room
    ksp(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
}