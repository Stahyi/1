package breakbadhabits.app.logic.habits.provider

import breakbadhabits.app.entity.Habit
import breakbadhabits.app.entity.HabitStatistics
import breakbadhabits.app.entity.HabitTrack
import breakbadhabits.app.logic.datetime.provider.DateTimeProvider
import breakbadhabits.foundation.datetime.countDays
import breakbadhabits.foundation.datetime.countDaysInMonth
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class HabitStatisticsProvider(
    private val habitTrackProvider: HabitTrackProvider,
    private val habitAbstinenceProvider: HabitAbstinenceProvider,
    private val dateTimeProvider: DateTimeProvider
) {
    fun habitStatisticsFlowById(
        habitId: Habit.Id
    ) = combine(
        habitAbstinenceProvider.provideAbstinenceListById(habitId),
        habitTrackProvider.provideByHabitId(habitId),
        dateTimeProvider.currentTimeFlow()
    ) { abstinenceList, tracks, currentTime ->
        if (tracks.isEmpty()) return@combine null

        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = Clock.System.now().toLocalDateTime(timeZone).date
        val previousMonthDate = currentDate.minus(DateTimeUnit.MONTH)

        HabitStatistics(
            habitId = habitId,
            abstinence = abstinenceList.let { list ->
                val timesInSeconds = list.map {
                    it.range.value.endInclusive.epochSeconds - it.range.value.start.epochSeconds
                }

                HabitStatistics.Abstinence(
                    averageTime = timesInSeconds.average().toDuration(DurationUnit.SECONDS),
                    maxTime = timesInSeconds.max().toDuration(DurationUnit.SECONDS),
                    minTime = timesInSeconds.min().toDuration(DurationUnit.SECONDS),
                    timeSinceFirstTrack = currentTime - tracks.minOf { it.range.value.start }
                )
            },
            eventCount = HabitStatistics.EventCount(
                currentMonthCount = tracks.countEventsInMonth(
                    year = currentDate.year,
                    month = currentDate.month,
                    timeZone = timeZone
                ),
                previousMonthCount = tracks.countEventsInMonth(
                    year = previousMonthDate.year,
                    month = previousMonthDate.month,
                    timeZone = timeZone
                ),
                totalCount = tracks.countEvents(timeZone)
            )
        )
    }
}

private fun List<HabitTrack>.countEventsInMonth(
    year: Int,
    month: Month,
    timeZone: TimeZone
) = filterByMonth(year, month, timeZone).fold(0) { total, track ->
    total + track.range.value.countDaysInMonth(year, month, timeZone) * track.eventCount.dailyCount
}

private fun List<HabitTrack>.countEvents(
    timeZone: TimeZone
) = fold(0) { total, track ->
    total + track.range.value.countDays(timeZone) * track.eventCount.dailyCount
}

private fun List<HabitTrack>.filterByMonth(
    year: Int,
    month: Month,
    timeZone: TimeZone
) = filter { track ->
    track.range.value.endInclusive.toLocalDateTime(timeZone).let {
        it.month == month && it.year == year
    } || track.range.value.start.toLocalDateTime(timeZone).let {
        it.month == month && it.year == year
    }
}