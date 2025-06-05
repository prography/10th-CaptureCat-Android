plugins {
    alias(libs.plugins.multi.module.android.library)
    alias(libs.plugins.multi.module.android.room)
    alias(libs.plugins.multi.module.android.application.hilt)
}

android {
    namespace = "com.prography.datastore"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore)
}