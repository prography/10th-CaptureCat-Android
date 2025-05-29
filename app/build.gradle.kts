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

    implementation(project(":presentation"))

    implementation(project(":core:navigation"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.serialization)

    implementation(libs.timber)
}