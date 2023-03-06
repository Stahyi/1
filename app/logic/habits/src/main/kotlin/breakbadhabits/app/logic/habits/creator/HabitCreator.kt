package breakbadhabits.app.logic.habits.creator

import breakbadhabits.app.database.AppDatabase
import breakbadhabits.app.database.IdGenerator
import breakbadhabits.app.entity.Habit
import breakbadhabits.app.logic.habits.serializer.HabitTrackSerializer
import breakbadhabits.app.logic.habits.validator.CorrectHabitNewName
import breakbadhabits.app.logic.habits.validator.CorrectHabitTrackRange
import breakbadhabits.app.logic.habits.validator.CorrectHabitTrackValue
import breakbadhabits.foundation.datetime.toMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitCreator(
    private val appDatabase: AppDatabase,
    private val idGenerator: IdGenerator,
    private val habitTrackSerializer: HabitTrackSerializer
) {
    suspend fun createHabit(
        name: CorrectHabitNewName,
        iconResource: Habit.IconResource,
        firstTrackValue: CorrectHabitTrackValue,
        firstTrackInterval: CorrectHabitTrackRange
    ) = withContext(Dispatchers.IO) {
        appDatabase.transaction {
            val habitId = idGenerator.nextId()
            val trackId = idGenerator.nextId()

            appDatabase.habitQueries.insert(
                id = habitId,
                name = name.data.value,
                iconId = iconResource.iconId
            )

            appDatabase.habitTrackQueries.insert(
                id = trackId,
                habitId = habitId,
                rangeStart = firstTrackInterval.data.value.start.toMillis(),
                rangeEnd = firstTrackInterval.data.value.endInclusive.toMillis(),
                eventCount = firstTrackValue.data.value.toLong(),
                eventCountTimeUnit = habitTrackSerializer.encodeEventCountTimeUnit(firstTrackValue.data.timeUnit),
                comment = null
            )
        }
    }
}