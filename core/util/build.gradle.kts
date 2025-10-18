plugins {
    alias(libs.plugins.multi.module.android.library.compose)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.prography.util"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(libs.android.permissions)
    implementation(libs.mixpanel)
    implementation(libs.app.update.ktx)
    implementation (libs.kotlinx.coroutines.play.services)
}