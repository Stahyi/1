package epicarchitect.breakbadhabits.ui.habits.widgets.list

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.intl.Locale

val LocalHabitWidgetsResources = compositionLocalOf {
    if (Locale.current.language == "ru") {
        RussianHabitWidgetsResources()
    } else {
        EnglishHabitWidgetsResources()
    }
}

interface HabitWidgetsResources {
    fun title(): String
    fun emptyList(): String
    fun deleteConfirmation(): String
}

class RussianHabitWidgetsResources : HabitWidgetsResources {
    override fun title() = "Виджеты"
    override fun emptyList() = "Виджеты отсутствуют. Вы можете добавить их на главный экран вашего телефона."
    override fun deleteConfirmation() =
        "Вы уверены что хотите удалить этот виджет? Вам придется удалить виджет с главного экрана вручную."
}

class EnglishHabitWidgetsResources : HabitWidgetsResources {
    override fun title() = "Widgets"
    override fun emptyList() = "There are no widgets. You can add them to your phone's home screen."
    override fun deleteConfirmation() =
        "Are you sure you want to delete this widget? You will have to manually remove the widget from the home screen."
}