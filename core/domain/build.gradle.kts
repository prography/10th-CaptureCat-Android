plugins {
    alias(libs.plugins.multi.module.jvm.library)
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.coroutines)
}