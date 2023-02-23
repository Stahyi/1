plugins {
    id("convention.android.library")
}

android {
    namespace = "breakbadhabits.app.presentation.dashboard"
}

dependencies {
    api(projects.framework.viewmodel)
    api(projects.app.logic.habits)
    api(projects.app.logic.datetime.formatter)
    api(projects.app.logic.datetime.provider)
}
