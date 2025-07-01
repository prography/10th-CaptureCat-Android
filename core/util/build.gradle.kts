plugins {
    alias(libs.plugins.multi.module.android.library.compose)
}

android {
    namespace = "com.prography.util"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(libs.android.permissions)
}