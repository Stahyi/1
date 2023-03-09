package breakbadhabits.app.presentation.habits

import androidx.lifecycle.viewModelScope
import breakbadhabits.app.entity.Habit
import breakbadhabits.app.logic.habits.deleter.HabitDeleter
import breakbadhabits.app.logic.habits.provider.HabitIconProvider
import breakbadhabits.app.logic.habits.provider.HabitProvider
import breakbadhabits.app.logic.habits.updater.HabitUpdater
import breakbadhabits.app.logic.habits.validator.CorrectHabitNewName
import breakbadhabits.app.logic.habits.validator.HabitNewNameValidator
import breakbadhabits.app.logic.habits.validator.ValidatedHabitNewName
import breakbadhabits.foundation.controller.RequestController
import breakbadhabits.foundation.controller.SingleSelectionController
import breakbadhabits.foundation.controller.ValidatedInputController
import breakbadhabits.foundation.viewmodel.ViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HabitUpdatingViewModel(
    private val habitProvider: HabitProvider,
    private val habitUpdater: HabitUpdater,
    private val habitDeleter: HabitDeleter,
    private val habitNameValidator: HabitNewNameValidator,
    habitIconProvider: HabitIconProvider,
    private val habitId: Habit.Id,
) : ViewModel() {

    private var initialHabit: Habit? = null

    val habitIconSelectionController = SingleSelectionController(
        coroutineScope = viewModelScope,
        items = habitIconProvider.provide(),
        default = List<Habit.Icon>::first
    )

    val habitNameController = ValidatedInputController(
        coroutineScope = viewModelScope,
        initialInput = Habit.Name(""),
        validation = {
            if (initialHabit == null) null
            else if (this == initialHabit?.name) null
            else habitNameValidator.validate(this)
        }
    )

    val updatingController = RequestController(
        coroutineScope = viewModelScope,
        request = {
            val habitIcon = habitIconSelectionController.state.value.selectedItem
            val habitName = habitNameController.validateAndAwait()
            require(habitName is CorrectHabitNewName)

            habitUpdater.updateHabit(
                habitId,
                habitName,
                habitIcon
            )
        },
        isAllowedFlow = combine(
            habitNameController.state,
            habitIconSelectionController.state,
        ) { name, icon ->
            (initialHabit?.name != name.input || initialHabit?.icon != icon.selectedItem)
                    && name.validationResult.let { it == null || it is CorrectHabitNewName }
        }
    )

    val deletionController = RequestController(
        coroutineScope = viewModelScope,
        request = {
            habitDeleter.deleteById(habitId)
        }
    )

    init {
        viewModelScope.launch {
            val habit = checkNotNull(habitProvider.provideHabitById(habitId))
            habitNameController.changeInput(habit.name)
            habitIconSelectionController.select(habit.icon)
            initialHabit = habit
        }
    }
}
