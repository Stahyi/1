package breakbadhabits.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.saveable.rememberSaveable
import breakbadhabits.feature.habits.presentation.EpicViewModel
import epicarchitect.epicstore.compose.rememberEpicStoreEntry
import java.util.UUID


@Composable
inline fun <reified T : EpicViewModel> rememberEpicViewModel(
    key: Any = rememberSaveable(init = UUID::randomUUID),
    noinline onCleared: ((T) -> Unit)? = null,
    noinline entry: @DisallowComposableCalls () -> T,
) = rememberEpicStoreEntry(
    key = key,
    onCleared = {
        it.clear()
        onCleared?.invoke(it)
    },
    entry = entry
)