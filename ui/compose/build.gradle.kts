plugins {
    id("convention.multiplatform.library")
    alias(libs.plugins.compose)
}

dependencies {
    commonMainApi("cafe.adriel.voyager:voyager-navigator:1.0.0-rc06")
    commonMainApi(projects.di.holder)
    commonMainApi(projects.foundation.uikit)
    commonMainApi(projects.presentation.dashboard)
    commonMainApi(projects.presentation.habits)
}