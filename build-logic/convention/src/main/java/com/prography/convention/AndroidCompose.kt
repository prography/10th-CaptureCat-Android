package com.prography.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.run {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("kotlin").get().toString()
        }

        dependencies {
            val bom = libs.findLibrary("compose.bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))

            // 기본 UI 구성요소
            add("implementation", libs.findLibrary("compose-ui").get())
            add("implementation", libs.findLibrary("compose-material3").get())
            add("implementation", libs.findLibrary("compose-navigation").get())
            add("implementation", libs.findLibrary("compose-viewmodel").get())

            // 디버그 도구
            add("debugImplementation", libs.findLibrary("debug-compose-tooling").get())
            add("implementation", libs.findLibrary("compose-tooling").get())
            add("implementation", libs.findLibrary("androidx-ui-tooling-preview-android").get())


            // 선택: 이미지 관련
            add("implementation", libs.findLibrary("compose-glide").get())


        }
    }
}
