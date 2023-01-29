plugins {
    id("convention.android.library")
}

android {
    namespace = "breakbadhabits.logic.habit.track.provider"
}

dependencies {
    api(project(":extensions:coroutines"))
    api(project(":entities"))
    implementation(project(":database"))
}