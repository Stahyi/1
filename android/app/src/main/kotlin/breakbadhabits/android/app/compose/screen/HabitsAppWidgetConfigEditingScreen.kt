package breakbadhabits.android.app.compose.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import breakbadhabits.android.app.App
import breakbadhabits.android.app.R
import breakbadhabits.android.app.viewmodel.HabitsAppWidgetConfigEditingViewModel
import breakbadhabits.android.compose.molecule.ActionType
import breakbadhabits.android.compose.molecule.Button
import breakbadhabits.android.compose.molecule.Card
import breakbadhabits.android.compose.molecule.Checkbox
import breakbadhabits.android.compose.molecule.Text
import breakbadhabits.android.compose.molecule.TextField
import breakbadhabits.android.compose.molecule.Title

@Composable
fun HabitsAppWidgetConfigEditingScreen(
    configId: Int,
    onFinished: () -> Unit
) {
    val habitsAppWidgetConfigEditingViewModel = viewModel {
        App.architecture.createHabitsAppWidgetConfigEditingViewModel(configId)
    }
    val alertDialogManager = App.architecture.alertDialogManager

    val habits by habitsAppWidgetConfigEditingViewModel.habitsFlow().collectAsState()
    val savingAllowed by habitsAppWidgetConfigEditingViewModel.savingAllowedStateFlow().collectAsState()
    val title by habitsAppWidgetConfigEditingViewModel.titleStateFlow().collectAsState()
    val saving by habitsAppWidgetConfigEditingViewModel.savingStateFlow().collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    if (saving is HabitsAppWidgetConfigEditingViewModel.SavingState.Executed) {
        LaunchedEffect(true) {
            onFinished()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = WindowInsets.systemBars.add(
                WindowInsets(
                    top = 16.dp,
                    left = 16.dp,
                    right = 16.dp,
                    bottom = 100.dp
                )
            ).asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Title(
                    text = stringResource(R.string.habitsAppWidgetConfigEditing_title)
                )
            }
            item {
                Text(
                    text = stringResource(R.string.habitsAppWidgetConfigEditing_name_description)
                )
            }
            item {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = title,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    onValueChange = {
                        habitsAppWidgetConfigEditingViewModel.updateTitle(it)
                    },
                    label = stringResource(R.string.habitsAppWidgetConfigEditing_name)
                )
            }
            item {
                Text(
                    text = stringResource(R.string.habitsAppWidgetConfigEditing_habitsDescription)
                )
            }
            items(habits) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                habitsAppWidgetConfigEditingViewModel.setCheckedHabit(item.id, !item.isChecked)
                            },
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = {
                                    habitsAppWidgetConfigEditingViewModel.setCheckedHabit(item.id, !item.isChecked)
                                }
                            )

                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = item.name
                            )
                        }
                    }
                }
            }

            item {
                Column {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(R.string.habitsAppWidgetConfigEditing_deletion_description)
                    )

                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = {
                            alertDialogManager.showAlert(
                                context,
                                title = null,
                                message = context.getString(R.string.habitsAppWidgets_deleteConfirmation),
                                positiveButtonTitle = context.getString(R.string.yes),
                                negativeButtonTitle = context.getString(R.string.cancel),
                                onPositive = {
                                    habitsAppWidgetConfigEditingViewModel.deleteWidget()
                                    onFinished()
                                },
                            )
                        },
                        text = stringResource(R.string.habitsAppWidgetConfigEditing_deletion_button),
                        actionType = ActionType.DANGEROUS
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .padding(
                    WindowInsets.navigationBars
                        .add(
                            WindowInsets(
                                bottom = 16.dp,
                                right = 16.dp
                            )
                        )
                        .asPaddingValues()
                )
                .align(Alignment.BottomEnd),
            onClick = {
                habitsAppWidgetConfigEditingViewModel.save()
            },
            enabled = savingAllowed,
            text = stringResource(R.string.habitsAppWidgetConfigEditing_finish),
            actionType = ActionType.MAIN
        )
    }
}