package breakbadhabits.android.app.compose.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import breakbadhabits.android.app.App
import breakbadhabits.android.app.R
import breakbadhabits.android.app.viewmodel.WidgetsViewModel
import breakbadhabits.android.compose.molecule.Card
import breakbadhabits.android.compose.molecule.Text
import breakbadhabits.android.compose.molecule.Title

@Composable
fun HabitsAppWidgetsScreen(
    openHabitAppWidgetConfigEditing: (configId: Int) -> Unit
) {
    val widgetsViewModel = viewModel {
        App.architecture.createWidgetsViewModel()
    }
    val widgets by widgetsViewModel.widgets.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (widgets.isEmpty()) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                text = stringResource(R.string.habitsAppWidgets_empty),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.main_widgets)
                    )
                }

                items(widgets) { item ->
                    WidgetConfigItem(
                        item,
                        onClick = {
                            openHabitAppWidgetConfigEditing(item.widgetConfig.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetConfigItem(
    item: WidgetsViewModel.WidgetItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClick()
                }
        ) {
            Title(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 54.dp
                ),
                text = item.widgetConfig.title.ifEmpty {
                    "#${item.widgetConfig.appWidgetId}"
                }
            )

            Text(
                modifier = Modifier.padding(
                    start = 16.dp,
                    bottom = 16.dp,
                    end = 16.dp
                ),
                text = buildString {
                    item.habits.forEachIndexed { index, habit ->
                        append(habit.name)
                        if (index != item.habits.lastIndex) {
                            appendLine()
                        }
                    }
                }
            )
        }
    }
}