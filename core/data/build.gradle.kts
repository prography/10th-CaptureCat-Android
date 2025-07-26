plugins {
    alias(libs.plugins.multi.module.android.library)
    alias(libs.plugins.multi.module.android.room)
    alias(libs.plugins.multi.module.android.application.hilt)

    kotlin("plugin.serialization") version "2.1.20"
}

android {
    namespace = "com.prography.data"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    implementation(libs.material)



    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.serialization)
}