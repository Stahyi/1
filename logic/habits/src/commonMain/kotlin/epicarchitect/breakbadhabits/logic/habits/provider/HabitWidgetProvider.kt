package epicarchitect.breakbadhabits.logic.habits.provider

import epicarchitect.breakbadhabits.database.asFlowOfList
import epicarchitect.breakbadhabits.database.asFlowOfOneOrNull
import epicarchitect.breakbadhabits.foundation.coroutines.CoroutineDispatchers
import epicarchitect.breakbadhabits.logic.habits.model.HabitWidget
import epicarchitect.breakbadhabits.sqldelight.main.MainDatabase
import epicarchitect.breakbadhabits.sqldelight.main.HabitWidget as DatabaseHabitWidget

class HabitWidgetProvider(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val mainDatabase: MainDatabase
) {
    fun provideFlowById(
        id: Int
    ) = mainDatabase.habitWidgetQueries
        .selectById(id)
        .asFlowOfOneOrNull(coroutineDispatchers, ::asHabitWidget)

    fun provideFlowBySystemId(
        systemId: Int
    ) = mainDatabase.habitWidgetQueries
        .selectBySystemId(systemId)
        .asFlowOfOneOrNull(coroutineDispatchers, ::asHabitWidget)

    fun provideAllFlow() = mainDatabase.habitWidgetQueries
        .selectAll()
        .asFlowOfList(coroutineDispatchers, ::asHabitWidget)

    private fun asHabitWidget(value: DatabaseHabitWidget) = with(value) {
        HabitWidget(
            id = id,
            title = title,
            systemId = systemId,
            habitIds = habitIds
        )
    }
}