package breakbadhabits.android.app.ui.habits.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import breakbadhabits.android.app.R
import breakbadhabits.app.entity.Habit
import breakbadhabits.app.entity.HabitAppWidgetConfig
import breakbadhabits.foundation.controller.MultiSelectionController
import breakbadhabits.foundation.controller.RequestController
import breakbadhabits.foundation.controller.ValidatedInputController
import breakbadhabits.foundation.uikit.Card
import breakbadhabits.foundation.uikit.Checkbox
import breakbadhabits.foundation.uikit.button.Button
import breakbadhabits.foundation.uikit.button.RequestButton
import breakbadhabits.foundation.uikit.effect.ClearFocusWhenKeyboardHiddenEffect
import breakbadhabits.foundation.uikit.ext.collectState
import breakbadhabits.foundation.uikit.text.Text
import breakbadhabits.foundation.uikit.text.TextFieldAdapter
import breakbadhabits.foundation.uikit.text.ValidatedInputField

@Composable
fun HabitAppWidgetUpdatingScreen(
    titleInputController: ValidatedInputController<HabitAppWidgetConfig.Title, Nothing>,
    habitsSelectionController: MultiSelectionController<Habit>,
    updatingController: RequestController,
    deletionController: RequestController
) {
    ClearFocusWhenKeyboardHiddenEffect()

    val habitsSelection by habitsSelectionController.collectState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.habitsAppWidgetConfigEditing_title),
            type = Text.Type.Title,
            priority = Text.Priority.High
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.habitsAppWidgetConfigEditing_name_description)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ValidatedInputField(
            controller = titleInputController,
            label = "Название",
            adapter = TextFieldAdapter(
                decodeInput = HabitAppWidgetConfig.Title::value,
                encodeInput = HabitAppWidgetConfig::Title,
                extractErrorMessage = { null }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.habitsAppWidgetConfigEditing_habitsDescription)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(habitsSelection.items.entries.toList(), key = { it.key.id.value }) {
                val (habit, isChecked) = it
                HabitItem(
                    habit = habit,
                    checked = isChecked,
                    onClick = {
                        habitsSelectionController.toggle(habit)
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.habitsAppWidgetConfigEditing_deletion_description)
        )

        Spacer(modifier = Modifier.height(16.dp))

        RequestButton(
            requestController = deletionController,
            text = stringResource(R.string.habitsAppWidgetConfigEditing_deletion_button),
            type = Button.Type.Dangerous
        )

        Spacer(modifier = Modifier.height(24.dp))

        RequestButton(
            modifier = Modifier.align(Alignment.End),
            requestController = updatingController,
            text = stringResource(R.string.habitsAppWidgetConfigEditing_finish),
            type = Button.Type.Main
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.HabitItem(
    habit: Habit,
    checked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateItemPlacement()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked,
                    onCheckedChange = { onClick() }
                )

                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = habit.name.value
                )
            }
        }
    }
}