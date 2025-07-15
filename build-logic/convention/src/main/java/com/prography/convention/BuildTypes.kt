package com.prography.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    extensionType: ExtensionType
) {
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }

        val properties = gradleLocalProperties(rootDir, providers)
        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(properties)
                            resValue("string", "kakao_native_app_key", "\"kakao${properties.getProperty("KAKAO_NATIVE_APP_KEY")}\"")
                        }
                        create("staging") {
                            configureStagingBuildType(properties)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, properties)
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(properties)
                        }
                        create("staging") {
                            configureStagingBuildType(properties)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, properties)
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType(properties: Properties) {
    buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
    buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_DEV_URL")}\"")
    buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${properties.getProperty("GOOGLE_WEB_CLIENT_ID")}\"")
    buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${properties.getProperty("KAKAO_NATIVE_APP_KEY")}\"")
}

private fun BuildType.configureStagingBuildType(properties: Properties) {
    buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
    buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_DEV_URL")}\"")
}

private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    properties: Properties
) {
    buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
    buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_RELEASE_URL")}\"")

    isMinifyEnabled = true
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
