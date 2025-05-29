package com.prography.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.addUILayerDependencies(project: Project) {
    add("implementation", project(":core:ui"))
    add("implementation", project(":core:util"))


    add("implementation", project.libs.findLibrary("compose-glide").get())
    add("implementation", project.libs.findLibrary("glide-okhttp3").get())
    add("debugImplementation", project.libs.findBundle("compose.debug").get())
    add("androidTestImplementation", project.libs.findLibrary("androidx.ui.test.junit4").get())
}