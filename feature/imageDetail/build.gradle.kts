plugins {
    alias(libs.plugins.multi.module.android.presentation.ui)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.prography.imageDetail"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:domain"))

    implementation(libs.coil.compose)
}