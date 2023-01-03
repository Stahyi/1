package breakbadhabits.presentation

import breakbadhabits.entity.HabitTrack
import breakbadhabits.logic.HabitTrackProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HabitTrackViewModel internal constructor(
    habitTrackProvider: HabitTrackProvider,
    habitTrackId: HabitTrack.Id
) : EpicViewModel() {

    val state = habitTrackProvider.provideFlow(habitTrackId).map {
        if (it == null) State.NotExist()
        else State.Loaded(it)
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), State.Loading())

    sealed class State {
        class Loading : State()
        class Loaded(val habitTrack: HabitTrack) : State()
        class NotExist : State()
    }
}