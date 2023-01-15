plugins {
    id("convention.android.application")
    id("convention.android.compose")
    id("kotlin-kapt")
}

android {
    namespace = "breakbadhabits.android.app"

    defaultConfig {
        applicationId = "kolmachikhin.alexander.breakbadhabits"
        versionCode = 70
        versionName = "4.0.0"
    }

    signingConfigs {
        create("release") {
            storeFile = file("signing/release.jks")
            storePassword = "epicdebug"
            keyAlias = "epicdebug"
            keyPassword = "epicdebug"
        }

        getByName("debug") {
            storeFile = file("signing/debug.jks")
            storePassword = "epicdebug"
            keyAlias = "epicdebug"
            keyPassword = "epicdebug"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
        }
    }
}

dependencies {
    implementation(project(":ui-kit"))
    implementation(project(":database"))
    implementation(project(":presentation:habit-creation"))
    implementation(project(":presentation:habit-deletion"))
    implementation(project(":presentation:habits-dashboard"))
    implementation(project(":presentation:habit-details"))
    implementation(project(":presentation:habit-track"))
    implementation(project(":presentation:habit-track-creation"))
}