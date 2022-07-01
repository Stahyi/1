package breakbadhabits.android.app.compose.screen

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import breakbadhabits.android.app.App
import breakbadhabits.android.app.R
import breakbadhabits.android.app.viewmodel.HabitEventCreationViewModel
import breakbadhabits.android.app.viewmodel.HabitViewModel
import breakbadhabits.android.compose.molecule.ActionType
import breakbadhabits.android.compose.molecule.Button
import breakbadhabits.android.compose.molecule.ErrorText
import breakbadhabits.android.compose.molecule.Text
import breakbadhabits.android.compose.molecule.TextField
import breakbadhabits.android.compose.molecule.Title
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

@Composable
fun HabitEventCreationScreen(
    habitId: Int,
    onFinished: () -> Unit
) {
    val habitViewModel = viewModel {
        App.architecture.createHabitViewModel(habitId)
    }
    val habitEventCreationViewModel = viewModel {
        App.architecture.createHabitEventCreationViewModel(habitId)
    }
    val dateTimeFormatter = App.architecture.dateTimeFormatter

    val comment by habitEventCreationViewModel.commentStateFlow.collectAsState()
    val creationAllowed by habitEventCreationViewModel.creationAllowedStateFlow.collectAsState()
    val creationDeferred by habitEventCreationViewModel.creationStateFlow.collectAsState()
    val time by habitEventCreationViewModel.timeStateFlow.collectAsState()
    val timeValidation by habitEventCreationViewModel.timeValidationStateFlow.collectAsState()
    val habitState by habitViewModel.habitFlow.collectAsState()
    val dateSelectionState = rememberMaterialDialogState()
    val timeSelectionState = rememberMaterialDialogState()

    LaunchedEffect(creationDeferred) {
        if (creationDeferred?.await() != null) {
            onFinished()
        }
    }

    MaterialDialog(
        dialogState = dateSelectionState,
        buttons = {
            positiveButton(stringResource(R.string.ok))
            negativeButton(stringResource(R.string.cancel))
        }
    ) {
        val calendar = Calendar.getInstance().apply {
            time.let(::setTimeInMillis)
        }

        datepicker(
            initialDate = LocalDate.of(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.DAY_OF_MONTH],
            ),
            title = stringResource(R.string.select_date)
        ) { date ->
            habitEventCreationViewModel.updateTime(
                Calendar.getInstance().apply {
                    time.let(::setTimeInMillis)
                    set(Calendar.YEAR, date.year)
                    set(Calendar.MONTH, date.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                }.timeInMillis
            )
        }
    }

    MaterialDialog(
        dialogState = timeSelectionState,
        buttons = {
            positiveButton(stringResource(R.string.ok))
            negativeButton(stringResource(R.string.cancel))
        }
    ) {
        val calendar = Calendar.getInstance().apply {
            time.let(::setTimeInMillis)
        }

        timepicker(
            initialTime = LocalTime.of(
                calendar[Calendar.HOUR],
                calendar[Calendar.MINUTE]
            ),
            is24HourClock = DateFormat.is24HourFormat(LocalContext.current),
            title = stringResource(R.string.select_time)
        ) { localTime ->
            habitEventCreationViewModel.updateTime(
                Calendar.getInstance().apply {
                    time.let(::setTimeInMillis)
                    set(Calendar.HOUR_OF_DAY, localTime.hour)
                    set(Calendar.MINUTE, localTime.minute)
                }.timeInMillis
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .padding(WindowInsets.systemBars.asPaddingValues())
                .fillMaxSize()
        ) {
            Title(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp),
                text = stringResource(R.string.habitEventCreation_title)
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                text = when (val state = habitState) {
                    is HabitViewModel.HabitState.Loaded -> stringResource(
                        R.string.habitEventCreation_habitName,
                        state.habit?.name ?: ""
                    )
                    is HabitViewModel.HabitState.Loading -> ""
                }
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = stringResource(R.string.habitEventCreation_event_description)
            )

            val calendar = Calendar.getInstance().apply {
                time.let(::setTimeInMillis)
            }

            Button(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                onClick = {
                    dateSelectionState.show()
                },
                text = stringResource(
                    R.string.habitEventCreation_eventDate,
                    dateTimeFormatter.formatDate(calendar)
                )
            )

            Button(
                modifier = Modifier.padding(start = 16.dp, top = 2.dp, end = 16.dp),
                onClick = {
                    timeSelectionState.show()
                },
                text = stringResource(
                    R.string.habitEventCreation_eventTime,
                    dateTimeFormatter.formatTime(calendar)
                )
            )

            (timeValidation as? HabitEventCreationViewModel.TimeValidationState.Executed)?.let {
                when (it.result) {
                    is HabitEventCreationViewModel.TimeValidationResult.BiggestThenCurrentTime -> {
                        ErrorText(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                            text = stringResource(R.string.habitEventCreation_eventTimeValidation_biggestThenCurrentTime)
                        )
                    }
                    else -> {
                        /* no-op */
                    }
                }
            }

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = stringResource(R.string.habitEventCreation_comment_description)
            )

            TextField(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .fillMaxWidth(),
                value = comment ?: "",
                onValueChange = {
                    habitEventCreationViewModel.updateComment(it)
                },
                label = stringResource(R.string.habitEventCreation_comment)
            )

            Spacer(modifier = Modifier.weight(1.0f))

            Text(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                    .align(Alignment.End),
                text = stringResource(R.string.habitEventCreation_finish_description)
            )

            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End),
                onClick = {
                    habitEventCreationViewModel.startCreation()
                },
                enabled = creationAllowed,
                text = stringResource(R.string.habitEventCreation_finish),
                actionType = ActionType.MAIN
            )
        }
    }
}
