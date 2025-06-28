plugins {
    alias(libs.plugins.multi.module.android.presentation.ui)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.android.start"
    compileSdk = 35
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:domain"))
}