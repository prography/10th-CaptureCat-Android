plugins {
    alias(libs.plugins.multi.module.android.library.compose)
    alias(libs.plugins.multi.module.android.application.hilt)
    alias(libs.plugins.multi.module.android.room)
}

android {
    namespace = "com.prography.presentation"
}

dependencies {

    implementation(project(":core:navigation"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(project(":feature:home"))
    implementation(project(":feature:auth"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.coroutines)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.serialization)

    implementation(libs.glide)
    implementation(libs.glide.okhttp3)
    implementation(libs.timber)

    testImplementation(libs.junit)
    annotationProcessor(libs.room.compiler)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}