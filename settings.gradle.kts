pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                include(":app", ":billing_module")

                 // Any other library modules
            } // settings.gradle.kts
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {pluginManagement {
        repositories { google(); mavenCentral(); gradlePluginPortal() }
        plugins {
            id("com.google.gms.google-services") version "4.4.2"
        }
    }

        google()
        mavenCentral()
    }
}



rootProject.name = "Task4App"
include(":app")
include(":yourlibrarymodule")
 