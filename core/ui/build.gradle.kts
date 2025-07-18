plugins {
    alias(libs.plugins.multi.module.android.library.compose)
}

android {
    namespace = "com.prography.ui"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}