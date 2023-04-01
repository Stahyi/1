package breakbadhabits.android.app.ui.habits.tracks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import breakbadhabits.android.app.R
import breakbadhabits.android.app.di.LocalLogicModule
import breakbadhabits.android.app.di.LocalUiModule
import breakbadhabits.app.logic.habits.entity.Habit
import breakbadhabits.app.logic.habits.entity.HabitTrack
import breakbadhabits.app.logic.habits.tracks.IncorrectHabitTrackEventCount
import breakbadhabits.app.logic.habits.tracks.IncorrectHabitTrackTime
import breakbadhabits.app.logic.habits.tracks.ValidatedHabitTrackEventCount
import breakbadhabits.app.logic.habits.tracks.ValidatedHabitTrackTime
import breakbadhabits.foundation.controller.LoadingController
import breakbadhabits.foundation.controller.SingleRequestController
import breakbadhabits.foundation.controller.ValidatedInputController
import breakbadhabits.foundation.datetime.withZeroSeconds
import breakbadhabits.foundation.uikit.Dialog
import breakbadhabits.foundation.uikit.LoadingBox
import breakbadhabits.foundation.uikit.LocalResourceIcon
import breakbadhabits.foundation.uikit.button.Button
import breakbadhabits.foundation.uikit.button.RequestButton
import breakbadhabits.foundation.uikit.calendar.SelectionEpicCalendarDialog
import breakbadhabits.foundation.uikit.calendar.rememberSelectionEpicCalendarState
import breakbadhabits.foundation.uikit.effect.ClearFocusWhenKeyboardHiddenEffect
import breakbadhabits.foundation.uikit.ext.collectState
import breakbadhabits.foundation.uikit.regex.Regexps
import breakbadhabits.foundation.uikit.text.ErrorText
import breakbadhabits.foundation.uikit.text.Text
import breakbadhabits.foundation.uikit.text.TextFieldAdapter
import breakbadhabits.foundation.uikit.text.ValidatedInputField

@Composable
fun HabitTrackUpdatingScreen(
    eventCountInputController: ValidatedInputController<HabitTrack.EventCount, ValidatedHabitTrackEventCount>,
    timeInputController: ValidatedInputController<HabitTrack.Time, ValidatedHabitTrackTime>,
    updatingController: SingleRequestController,
    deletionController: SingleRequestController,
    habitController: LoadingController<Habit?>,
    commentInputController: ValidatedInputController<HabitTrack.Comment?, Nothing>
) {
    val logicModule = LocalLogicModule.current
    val uiModule = LocalUiModule.current
    val dateTimeConfigProvider = logicModule.dateTimeConfigProvider
    val dateTimeConfigState = dateTimeConfigProvider.configFlow().collectAsState(initial = null)
    val dateTimeConfig = dateTimeConfigState.value ?: return

    val dateTimeFormatter = uiModule.dateTimeFormatter
    var rangeSelectionShow by remember { mutableStateOf(false) }
    val eventCountState by eventCountInputController.collectState()
    val rangeState by timeInputController.collectState()

    ClearFocusWhenKeyboardHiddenEffect()

    var deletionShow by remember { mutableStateOf(false) }
    if (deletionShow) {
        Dialog(onDismiss = { deletionShow = false }) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.habitEvents_deleteConfirmation),
                    type = Text.Type.Description,
                    priority = Text.Priority.High
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Button(
                        text = stringResource(R.string.cancel),
                        onClick = {
                            deletionShow = false
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    RequestButton(
                        controller = deletionController,
                        text = stringResource(R.string.yes),
                        type = Button.Type.Main
                    )
                }
            }
        }
    }

    if (rangeSelectionShow) {
        val epicCalendarState = rememberSelectionEpicCalendarState(
            timeZone = dateTimeConfig.appTimeZone,
            initialRange = rangeState.input
        )

        SelectionEpicCalendarDialog(
            state = epicCalendarState,
            onSelected = {
                rangeSelectionShow = false
                timeInputController.changeInput(
                    HabitTrack.Time.of(
                        it.withZeroSeconds(dateTimeConfig.appTimeZone)
                    )
                )
            },
            onCancel = {
                rangeSelectionShow = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.habitEventEditing_title),
            type = Text.Type.Title,
            priority = Text.Priority.High
        )

        Spacer(Modifier.height(8.dp))

        LoadingBox(habitController) {
            if (it != null) {
                Text(
                    text = stringResource(
                        R.string.habitEventEditing_habitName,
                        it.name.value
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(text = "Укажите сколько примерно было событий привычки каждый день")

        Spacer(Modifier.height(16.dp))

        ValidatedInputField(
            controller = eventCountInputController,
            adapter = remember {
                TextFieldAdapter(
                    decodeInput = { it.dailyCount.toString() },
                    encodeInput = {
                        eventCountState.input.copy(
                            dailyCount = it.toIntOrNull() ?: 0
                        )
                    },
                    extractErrorMessage = {
                        val incorrect = (it as? IncorrectHabitTrackEventCount)
                            ?: return@TextFieldAdapter null
                        when (incorrect.reason) {
                            is IncorrectHabitTrackEventCount.Reason.Empty -> "Поле не может быть пустым"
                        }
                    }
                )
            },
            label = "Число событий в день",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            regex = Regexps.integersOrEmpty(maxCharCount = 4)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Укажите даты первого и последнего события привычки."
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { rangeSelectionShow = true },
            text = rangeState.input.let {
                when (it) {
                    is HabitTrack.Time.Date -> {
                        val start = dateTimeFormatter.formatDateTime(it.start)
                        "Дата и время: $start"
                    }
                    is HabitTrack.Time.Range -> {
                        val start = dateTimeFormatter.formatDateTime(it.start)
                        val end = dateTimeFormatter.formatDateTime(it.endInclusive)
                        "Первое событие: $start, последнее событие: $end"
                    }
                }
            }
        )

        (rangeState.validationResult as? IncorrectHabitTrackTime)?.let {
            Spacer(Modifier.height(8.dp))
            when (it.reason) {
                IncorrectHabitTrackTime.Reason.BiggestThenCurrentTime -> {
                    ErrorText(text = "Нельзя выбрать время больше чем текущее")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.habitEventEditing_comment_description)
        )


        Spacer(Modifier.height(16.dp))

        ValidatedInputField(
            label = stringResource(R.string.habitEventEditing_comment),
            controller = commentInputController,
            adapter = remember {
                TextFieldAdapter(
                    decodeInput = { it?.value ?: "" },
                    encodeInput = { if (it.isEmpty()) null else HabitTrack.Comment(it) },
                    extractErrorMessage = { null }
                )
            }
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.habitEventEditing_deletion_description)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            text = stringResource(R.string.habitEventEditing_deletion_button),
            type = Button.Type.Dangerous,
            onClick = {
                deletionShow = true
            }
        )

        Spacer(modifier = Modifier.weight(1.0f))

        Spacer(modifier = Modifier.height(48.dp))

        RequestButton(
            modifier = Modifier.align(Alignment.End),
            controller = updatingController,
            text = stringResource(R.string.habitEventEditing_finish),
            type = Button.Type.Main,
            icon = {
                LocalResourceIcon(resourceId = R.drawable.ic_done)
            }
        )
    }
}