plugins {
    alias(libs.plugins.multi.module.android.presentation.ui)
}

android {
    namespace = "com.prography.auth"
}

dependencies {
    implementation(project(":core:navigation"))
}