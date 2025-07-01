import java.util.Properties

plugins {
    alias(libs.plugins.multi.module.android.application.compose)
    alias(libs.plugins.multi.module.android.application.hilt)
    alias(libs.plugins.multi.module.android.room)

    id("com.google.gms.google-services")
    id("com.vanniktech.dependency.graph.generator") version "0.8.0"
    kotlin("plugin.serialization") version "2.1.20"
}

android {
    namespace = "com.android.prography"
    defaultConfig {
        applicationId = "com.android.prography"
    }
}

dependencies {
    implementation(project(":presentation"))

    implementation(libs.timber)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.kakao.all)
}