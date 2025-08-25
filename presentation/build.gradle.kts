plugins {
    alias(libs.plugins.multi.module.android.library.compose)
    alias(libs.plugins.multi.module.android.application.hilt)
    alias(libs.plugins.multi.module.android.room)
    alias(libs.plugins.multi.module.android.presentation.ui)
}

android {
    namespace = "com.prography.presentation"
}

dependencies {

    implementation(project(":core:navigation"))
    implementation(project(":core:domain"))


    implementation(project(":feature:favorite"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:home"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:organize"))
    implementation(project(":feature:start"))
    implementation(project(":feature:setting"))
    implementation(project(":feature:imageDetail"))
    implementation(project(":feature:tag"))

    implementation(libs.splashscreen)
    // Firebase Remote Config
    implementation(platform(libs.firebase.bom))
    // Coroutines Task await
    implementation(libs.coroutines)
    implementation(libs.firebase.config)
    implementation(libs.google.firebase.analytics)
    implementation(libs.app.update.ktx)
}
