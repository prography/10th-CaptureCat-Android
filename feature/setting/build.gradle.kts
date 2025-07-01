plugins {
    alias(libs.plugins.multi.module.android.presentation.ui)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.prography.setting"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
}