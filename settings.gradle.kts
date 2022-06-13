pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

rootProject.name = "mirror-parent"
include("mirror-paper", "mirror-core")
