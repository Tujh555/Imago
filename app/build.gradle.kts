import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "io.tujh.imago"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.tujh.imago"
        minSdk = 29
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

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.StrongSkipping)
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
    implementation(libs.coil)
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.model)
    implementation(libs.voyager.bottomsheet)
    implementation(libs.voyager.tab)
    implementation(libs.voyager.hilt)
    implementation(libs.voyager.transitions)
    implementation(libs.work.manager)
    implementation(libs.work.manager.ktx)
    implementation(libs.google.hilt.android)
    implementation(libs.gson)
    implementation(libs.gson.converter)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.coil.network)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.compose.extended.gestures)
    implementation(libs.androidx.exifinterface)
    implementation(libs.compose.colorful.sliders)
    implementation(libs.compose.color.picker.bundle) {
        exclude(group = "com.github.SmartToolFactory", module = "Compose-Image-Cropper")
    }
    implementation(libs.compose.animatedlist)
    implementation(libs.lottie)
    implementation(libs.hilt.work)
    implementation(libs.compose.shimmer)
    implementation(libs.reorderable)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}