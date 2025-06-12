plugins {
    alias(libs.plugins.multi.module.android.presentation.ui)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.prography.home"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    implementation(libs.android.permissions)
}
