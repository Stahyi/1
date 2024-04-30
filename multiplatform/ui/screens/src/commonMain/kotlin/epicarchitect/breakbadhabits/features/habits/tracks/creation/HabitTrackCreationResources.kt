package epicarchitect.breakbadhabits.features.habits.tracks.creation

import epicarchitect.breakbadhabits.validator.IncorrectHabitTrackEventCount

interface HabitTrackCreationResources {
    fun titleText(): String
    fun commentDescription(): String
    fun commentLabel(): String
    fun finishDescription(): String
    fun finishButton(): String
    fun habitNameLabel(habitName: String): String
    fun trackEventCountError(reason: IncorrectHabitTrackEventCount.Reason): String
}