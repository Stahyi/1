package breakbadhabits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import breakbadhabits.entity.Habit
import breakbadhabits.logic.HabitProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HabitDetailsViewModel(
    habitProvider: HabitProvider,
    habitId: Habit.Id
) : ViewModel() {

    val state = habitProvider.provideHabitFlowById(habitId).map {
        if (it == null) State.NotExist()
        else State.Loaded(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), State.Loading())

    sealed class State {
        class Loading : State()
        class Loaded(val habit: Habit) : State()
        class NotExist : State()
    }
}