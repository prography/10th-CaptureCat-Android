plugins {
    alias(libs.plugins.multi.module.android.presentation.ui)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.prography.auth"
}

dependencies {
    implementation(project(":core:navigation"))
}