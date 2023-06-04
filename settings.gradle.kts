enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "breakbadhabits"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

includeBuild("build-logic")

include(
    ":android-app",
    ":database",

    ":logic:habits",
    ":logic:datetime",

    ":presentation:dashboard",
    ":presentation:habits",

    ":foundation:controllers",
    ":foundation:coroutines",
    ":foundation:datetime",
    ":foundation:uikit",
    ":foundation:viewmodel",
    ":foundation:math",
    ":foundation:icons",
)