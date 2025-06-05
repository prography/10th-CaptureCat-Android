import java.util.Properties

plugins {
    alias(libs.plugins.multi.module.android.application.compose)
    alias(libs.plugins.multi.module.android.application.hilt)
    alias(libs.plugins.multi.module.android.room)

    kotlin("plugin.serialization") version "2.1.10"
}

android {
    namespace = "com.android.prography"
    defaultConfig {
        applicationId = "com.android.prography"
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":presentation"))
    implementation(libs.timber)
}