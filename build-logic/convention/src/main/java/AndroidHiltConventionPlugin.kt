import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import com.prography.convention.libs

internal class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.run {
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }

            // Android plugin이 적용된 이후에 dependencies 블록을 추가
            plugins.whenPluginAdded {
                configureHiltDependencies()
            }
        }
    }

    private fun Project.configureHiltDependencies() {
        dependencies {
            "implementation"(libs.findLibrary("hilt-android").get())
            add("implementation", libs.findLibrary("hilt-compose").get())
            "ksp"(libs.findLibrary("hilt-compiler").get())
        }
    }
}