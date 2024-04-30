package epicarchitect.breakbadhabits.newarch.habits

import kotlinx.datetime.Instant

interface HabitAbstinenceHistory {
    fun failedRanges(): List<ClosedRange<Instant>>
    fun abstinenceRanges(): List<ClosedRange<Instant>>
}