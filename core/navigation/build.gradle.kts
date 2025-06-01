plugins {
    alias(libs.plugins.multi.module.android.library.compose)
    kotlin("plugin.serialization") version "2.1.20"
}

android {
    namespace = "com.prography.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines)
    implementation(libs.javax.inject)
}