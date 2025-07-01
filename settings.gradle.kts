pluginManagement {

    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { setUrl("https://jitpack.io")  }
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }
        mavenCentral()
    }
}

rootProject.name = "prography"
include(":app")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core:util")
include(":core:navigation")
include(":feature:home")
include(":feature:auth")
include(":presentation")
include(":core:datastore")
include(":core:database")
include(":core:network")
include(":feature:onboarding")
include(":feature:organize")
include(":feature:start")
include(":feature:setting")
