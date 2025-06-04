plugins {
    alias(libs.plugins.multi.module.android.library)
    alias(libs.plugins.multi.module.android.room)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.prography.database"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.gson)
}