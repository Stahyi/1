package breakbadhabits.app.logic.datetime.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class DateTimeProvider(updatePeriodMillis: () -> Long) {
    private val currentTime = MutableStateFlow(getCurrentTime())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                currentTime.value = getCurrentTime()
                delay(updatePeriodMillis())
            }
        }
    }

    fun currentTimeFlow(): Flow<Instant> = currentTime

    fun getCurrentTime() = Clock.System.now()
}