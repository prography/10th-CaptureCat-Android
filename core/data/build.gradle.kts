plugins {
    alias(libs.plugins.multi.module.android.library)
    alias(libs.plugins.multi.module.android.room)
    alias(libs.plugins.multi.module.android.application.hilt)

    kotlin("plugin.serialization") version "2.1.10"
}

android {
    namespace = "com.prography.data"
}

dependencies {
    implementation(project(":core:util"))
    implementation(project(":core:domain"))

    implementation(libs.material)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)


    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.serialization)
}