package breakbadhabits.android.app.ui.habits

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import breakbadhabits.android.app.R
import breakbadhabits.android.app.format.DurationFormatter
import breakbadhabits.android.app.ui.app.LocalDateTimeConfigProvider
import breakbadhabits.android.app.ui.app.LocalDateTimeFormatter
import breakbadhabits.android.app.ui.app.LocalDurationFormatter
import breakbadhabits.android.app.ui.app.LocalHabitIconResourceProvider
import breakbadhabits.app.entity.Habit
import breakbadhabits.app.entity.HabitAbstinence
import breakbadhabits.app.entity.HabitStatistics
import breakbadhabits.app.entity.HabitTrack
import breakbadhabits.foundation.controller.LoadingController
import breakbadhabits.foundation.datetime.toDuration
import breakbadhabits.foundation.uikit.Card
import breakbadhabits.foundation.uikit.Histogram
import breakbadhabits.foundation.uikit.IconButton
import breakbadhabits.foundation.uikit.LoadingBox
import breakbadhabits.foundation.uikit.LocalResourceIcon
import breakbadhabits.foundation.uikit.StatisticData
import breakbadhabits.foundation.uikit.Statistics
import breakbadhabits.foundation.uikit.button.Button
import breakbadhabits.foundation.uikit.button.InteractionType
import breakbadhabits.foundation.uikit.calendar.EpicCalendar
import breakbadhabits.foundation.uikit.calendar.rememberEpicCalendarState
import breakbadhabits.foundation.uikit.text.Text
import breakbadhabits.foundation.uikit.text.Title
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.time.Duration.Companion.seconds

@Composable
fun HabitDetailsScreen(
    habitController: LoadingController<Habit?>,
    habitAbstinenceController: LoadingController<HabitAbstinence?>,
    abstinenceListController: LoadingController<List<HabitAbstinence>>,
    statisticsController: LoadingController<HabitStatistics?>,
    habitTracksController: LoadingController<List<HabitTrack>>,
    onEditClick: () -> Unit,
    onAddTrackClick: () -> Unit,
    onAllTracksClick: () -> Unit
) {
    val dateTimeConfigProvider = LocalDateTimeConfigProvider.current
    val dateTimeConfigState = dateTimeConfigProvider.configFlow().collectAsState(initial = null)
    val dateTimeConfig = dateTimeConfigState.value ?: return

    val dateTimeFormatter = LocalDateTimeFormatter.current
    val habitIconResources = LocalHabitIconResourceProvider.current
    val durationFormatter = LocalDurationFormatter.current
    val context = LocalContext.current

    LoadingBox(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        controller = habitController
    ) { habit ->
        habit ?: return@LoadingBox

        Column {
            Spacer(modifier = Modifier.height(16.dp))

            LocalResourceIcon(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.CenterHorizontally),
                resourceId = habitIconResources[habit.icon].resourceId
            )

            Spacer(modifier = Modifier.height(8.dp))

            Title(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = habit.name.value
            )

            Spacer(modifier = Modifier.height(8.dp))

            LoadingBox(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                controller = habitAbstinenceController
            ) { abstinence ->
                Text(
                    text = abstinence?.let {
                        durationFormatter.format(it.range.toDuration())
                    } ?: stringResource(R.string.habits_noEvents)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onAddTrackClick,
                text = stringResource(R.string.habit_resetTime),
                interactionType = InteractionType.MAIN
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                LoadingBox(habitTracksController) { tracks ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val yearMonth = remember { YearMonth.now() }
                        val epicCalendarState = rememberEpicCalendarState(
                            yearMonth = yearMonth,
                            ranges = remember(tracks) {
                                tracks.map {
                                    it.time.start
                                        .toLocalDateTime(dateTimeConfig.systemTimeZone)
                                        .toJavaLocalDateTime()
                                        .toLocalDate()..it.time.endInclusive
                                        .toLocalDateTime(dateTimeConfig.systemTimeZone)
                                        .toJavaLocalDateTime()
                                        .toLocalDate()
                                }
                            }
                        )
                        val title = remember(yearMonth) {
                            "${
                                yearMonth.month.getDisplayName(
                                    TextStyle.FULL_STANDALONE,
                                    Locale.getDefault()
                                ).replaceFirstChar { it.titlecase(Locale.getDefault()) }
                            } ${yearMonth.year}"
                        }

                        Title(
                            modifier = Modifier.padding(16.dp),
                            text = title
                        )

                        EpicCalendar(
                            state = epicCalendarState,
                            horizontalInnerPadding = 8.dp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clickable(onClick = onAllTracksClick)
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                fontWeight = FontWeight.Medium,
                                text = stringResource(R.string.habit_showAllEvents)
                            )
                        }
                    }
                }
            }

            LoadingBox(abstinenceListController) { abstinenceList ->
                if (abstinenceList.size < 3) return@LoadingBox
                Card(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Title(
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                            text = stringResource(R.string.habitAnalyze_abstinenceChart_title)
                        )

                        val abstinenceTimes = remember(abstinenceList) {
                            abstinenceList.map {
                                it.range.toDuration().inWholeSeconds.toFloat()
                            }
                        }

                        Histogram(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            values = abstinenceTimes,
                            valueFormatter = {
                                durationFormatter.format(
                                    duration = it.toLong().seconds,
                                    accuracy = DurationFormatter.Accuracy.HOURS
                                )
                            },
                            startPadding = 16.dp,
                            endPadding = 16.dp,
                        )
                    }
                }
            }

            LoadingBox(statisticsController) { statistics ->
                statistics ?: return@LoadingBox
                Card(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .fillMaxWidth()
                    ) {
                        Title(stringResource(R.string.habitAnalyze_statistics_title))

                        Spacer(modifier = Modifier.height(8.dp))

                        Statistics(
                            modifier = Modifier.fillMaxWidth(),
                            statistics = remember(statistics) {
                                statistics.toStatisticsData(
                                    context,
                                    durationFormatter
                                )
                            }
                        )
                    }
                }
            }

            Text(
                modifier = Modifier.padding(24.dp),
                text = "Привычка создана:\n${
                    dateTimeFormatter.formatDateTime(habit.creationTime.time)
                } " + dateTimeFormatter.formatTimeZoneIfNotSystemOrEmpty(habit.creationTime.timeZone)
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onEditClick
        ) {
            LocalResourceIcon(R.drawable.ic_settings)
        }
    }
}

private fun HabitStatistics.toStatisticsData(
    context: Context,
    durationFormatter: DurationFormatter,
) = listOf(
    StatisticData(
        name = context.getString(R.string.habitAnalyze_statistics_averageAbstinenceTime),
        value = durationFormatter.format(
            duration = abstinence.averageTime,
            accuracy = DurationFormatter.Accuracy.HOURS
        )
    ),
    StatisticData(
        name = context.getString(R.string.habitAnalyze_statistics_maxAbstinenceTime),
        value = durationFormatter.format(
            duration = abstinence.maxTime,
            accuracy = DurationFormatter.Accuracy.HOURS
        )
    ),
    StatisticData(
        name = context.getString(R.string.habitAnalyze_statistics_minAbstinenceTime),
        value = durationFormatter.format(
            duration = abstinence.minTime,
            accuracy = DurationFormatter.Accuracy.HOURS
        )
    ),
    StatisticData(
        name = context.getString(R.string.habitAnalyze_statistics_timeFromFirstEvent),
        value = durationFormatter.format(
            duration = abstinence.timeSinceFirstTrack,
            accuracy = DurationFormatter.Accuracy.HOURS
        )
    ),
    StatisticData(
        name = context.getString(R.string.habitAnalyze_statistics_countEventsInCurrentMonth),
        value = eventCount.currentMonthCount.toString()
    ),
    StatisticData(
        name = context.getString(R.string.habitAnalyze_statistics_countEventsInPreviousMonth),
        value = eventCount.previousMonthCount.toString()
    ),
    StatisticData(
        name = context.getString(R.string.habitAnalyze_statistics_countEvents),
        value = eventCount.totalCount.toString()
    )
)