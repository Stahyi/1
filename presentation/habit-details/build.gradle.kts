plugins {
    id("convention.android.library")
}

android {
    namespace = "breakbadhabits.presentation.habit.details"
}

dependencies {
    api(project(":logic:habit-provider"))
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
}