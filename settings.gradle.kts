pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven { url = uri("https://repo1.maven.org/maven2") }
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    //repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // Mirrors / fallbacks:
        maven { url = uri("https://repo1.maven.org/maven2") }
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2") }
        mavenCentral()
    }
}

rootProject.name = "QuakeWatch"
include(":app")
