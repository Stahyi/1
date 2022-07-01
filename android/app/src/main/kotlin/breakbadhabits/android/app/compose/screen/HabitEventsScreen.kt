package breakbadhabits.android.app.compose.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import breakbadhabits.android.app.App
import breakbadhabits.android.app.R
import breakbadhabits.android.app.viewmodel.HabitViewModel
import breakbadhabits.android.compose.molecule.Icon
import breakbadhabits.android.compose.molecule.Text
import breakbadhabits.android.compose.molecule.Title
import breakbadhabits.compose.organism.events.calendar.EventData
import breakbadhabits.compose.organism.events.calendar.EventsCalendar
import breakbadhabits.compose.organism.events.calendar.rememberEventsCalendarState
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

@Composable
fun HabitEventsScreen(
    habitId: Int,
    openHabitEventEditing: (habitEventId: Int) -> Unit,
) {
    val habitViewModel = viewModel {
        App.architecture.createHabitViewModel(habitId)
    }
    val habitEventsViewModel = viewModel {
        App.architecture.createHabitEventsViewModel(habitId)
    }
    val dateTimeFormatter = App.architecture.dateTimeFormatter
    val habitIconResources = App.architecture.habitIconResources

    val habitState by habitViewModel.habitFlow.collectAsState()
    val calendarState = rememberEventsCalendarState()
    val allHabitEvents by habitEventsViewModel.habitEventStateFlow.collectAsState()
    val habitEvents = allHabitEvents.filter {
        Instant.ofEpochMilli(it.timeInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .monthValue == calendarState.value.monthValue
    }.sortedByDescending { it.timeInMillis }

    val habit = (habitState as? HabitViewModel.HabitState.Loaded)?.habit

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.add(
            WindowInsets(
                top = 32.dp,
                left = 16.dp,
                right = 16.dp,
                bottom = 32.dp
            )
        ).asPaddingValues()
    ) {
        if (habit != null) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(habitIconResources[habit.iconId])
                    )

                    Title(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        text = habit.name
                    )
                }
            }
        }

        item {
            EventsCalendar(
                modifier = Modifier.padding(horizontal = 8.dp),
                calendarState = calendarState,
                events = habitEvents.map {
                    EventData(
                        it.id,
                        it.timeInMillis
                    )
                }
            )
        }

        items(habitEvents) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openHabitEventEditing(item.id)
                    }
            ) {
                Column(
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = dateTimeFormatter.formatDateTime(
                            Calendar.getInstance().apply {
                                timeInMillis = item.timeInMillis
                            },
                            withoutYear = true
                        )
                    )

                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = if (item.comment.isNullOrEmpty()) {
                            stringResource(R.string.habitEvents_noComment)
                        } else {
                            item.comment
                        }
                    )
                }
            }
        }
    }
}