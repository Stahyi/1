package breakbadhabits.logic

import breakbadhabits.database.AppDatabase
import breakbadhabits.database.IdGenerator
import breakbadhabits.entity.Habit
import breakbadhabits.extension.datetime.toMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitCreator(
    private val appDatabase: AppDatabase,
    private val idGenerator: IdGenerator
) {
    suspend fun createHabit(
        name: CorrectHabitNewNewName,
        iconResource: Habit.IconResource,
        countability: HabitCountability,
        firstTrackInterval: CorrectHabitTrackInterval
    ) = withContext(Dispatchers.IO) {
        appDatabase.transaction {
            val habitId = idGenerator.nextId()
            val trackId = idGenerator.nextId()

            appDatabase.habitQueries.insert(
                id = habitId,
                name = name.data.value,
                iconId = iconResource.iconId,
                isCountable = countability is HabitCountability.Countable
            )

            appDatabase.habitTrackQueries.insert(
                id = trackId,
                habitId = habitId,
                rangeStart = firstTrackInterval.data.value.start.toMillis(),
                rangeEnd = firstTrackInterval.data.value.endInclusive.toMillis(),
                dailyCount = when (countability) {
                    is HabitCountability.Countable -> countability.averageDailyCount.value
                    is HabitCountability.Uncountable -> 1.0
                },
                comment = null
            )
        }
    }
}