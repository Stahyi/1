package epicarchitect.breakbadhabits.entity.datetime

import epicarchitect.breakbadhabits.entity.math.ranges.ascended
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToLong
import kotlin.time.DurationUnit
import kotlin.time.toDuration

//
// val ZonedDateTimeRange.duration get() = endInclusive.instant - start.instant
//
// fun ZonedDateTimeRange.split(step: Duration): List<ZonedDateTimeRange> {
//    var current = start
//    return buildList {
//        while (current < endInclusive) {
//            val newEnd = (current.instant + step).let {
//                if (it < endInclusive.instant) {
//                    it
//                } else {
//                    endInclusive.instant
//                }
//            }
//            add(
//                ZonedDateTimeRange(
//                    current,
//                    current.copy(
//                        instant = newEnd
//                    ).also { current = it }
//                )
//            )
//        }
//    }
// }
//
// fun ZonedDateTimeRange.countDays() =
//    start.instant.daysUntil(endInclusive.instant, timeZone) + 1

fun ClosedRange<LocalDateTime>.duration(timeZone: TimeZone) = ascended().let {
    it.endInclusive.toInstant(timeZone) - it.start.toInstant(timeZone)
}

fun ClosedRange<LocalDateTime>.numberOfDays(
    timeZone: TimeZone
): Int = start.toInstant(timeZone).daysUntil(
    endInclusive.toInstant(timeZone),
    timeZone
) + 1

fun InstantRange.countDays(timeZone: TimeZone) =
    start.daysUntil(endInclusive, timeZone) + 1

fun InstantRange.countDaysInMonth(
    monthOfYear: MonthOfYear,
    timeZone: TimeZone
): Int {
    val month = monthOfYear.month
    val year = monthOfYear.year
    val startDate = start.toLocalDateTime(timeZone).date
    val endDate = endInclusive.toLocalDateTime(timeZone).date
    val lengthOfMonth = monthOfYear.numberOfDays

    val startDateInMonth = startDate.year == year && startDate.month == month
    val startDateBeforeMonth = startDate.year < year || startDate.month < month
    val endDateInMonth = endDate.year == year && endDate.month == month
    val endDateAfterMonth = endDate.year > year || endDate.month > month

    return when {
        startDateInMonth && endDateInMonth -> {
            countDays(timeZone)
        }

        startDateInMonth && endDateAfterMonth -> {
            lengthOfMonth - (startDate.dayOfMonth - 1)
        }

        startDateBeforeMonth && endDateInMonth -> {
            lengthOfMonth - (lengthOfMonth - endDate.dayOfMonth)
        }

        startDateBeforeMonth && endDateAfterMonth -> {
            lengthOfMonth
        }

        else -> 0
    }
}

// copied from IsoChronology

fun isLeapYear(year: Long): Boolean {
    return year and 3L == 0L && (year % 100 != 0L || year % 400 == 0L)
}
//
// operator fun ZonedDateTime.minus(duration: Duration) = ZonedDateTime(
//    instant = instant - duration,
//    timeZone = timeZone
// )
//
// operator fun ZonedDateTime.minus(other: ZonedDateTime) = instant - other.instant

fun List<ClosedRange<Instant>>.averageDuration() = map {
    it.endInclusive.epochSeconds - it.start.epochSeconds
}.let {
    if (it.size > 1) {
        it.average().roundToLong()
    } else if (it.size == 1) {
        it.first()
    } else {
        null
    }
}?.toDuration(DurationUnit.SECONDS)

fun List<ClosedRange<Instant>>.maxDuration() = maxOfOrNull { it.endInclusive - it.start }

fun List<ClosedRange<Instant>>.minDuration() = minOfOrNull { it.endInclusive - it.start }

fun ClosedRange<Instant>.duration() = endInclusive - start
//
// fun List<ZonedDateTimeRange>.averageDuration() = map {
//    it.endInclusive.instant.epochSeconds - it.start.instant.epochSeconds
// }.let {
//    if (it.size > 1) {
//        it.average().roundToLong()
//    } else {
//        it.first()
//    }
// }.toDuration(DurationUnit.SECONDS)
//
// fun List<ZonedDateTimeRange>.maxDuration() = maxOf { it.duration }
//
// fun List<ZonedDateTimeRange>.minDuration() = minOf { it.duration }

fun List<InstantRange>.toLocalDateRanges(timeZone: TimeZone) = map {
    it.start.toLocalDateTime(timeZone).date..it.endInclusive.toLocalDateTime(timeZone).date
}
//
// fun ZonedDateTimeRange.withZeroSeconds() =
//    start.withZeroSeconds()..endInclusive.withZeroSeconds()

fun InstantRange.withZeroSeconds(timeZone: TimeZone) =
    start.withZeroSeconds(timeZone)..endInclusive.withZeroSeconds(timeZone)
//
// fun ZonedDateTime.withZeroSeconds() = ZonedDateTime(
//    instant.withZeroSeconds(timeZone),
//    timeZone
// )

fun Instant.withZeroSeconds(timeZone: TimeZone): Instant {
    val initial = toLocalDateTime(timeZone)
    val fixed = LocalDateTime(
        date = initial.date,
        time = LocalTime(
            hour = initial.hour,
            minute = initial.minute,
            second = 0,
            nanosecond = 0
        )
    )
    return fixed.toInstant(timeZone)
}