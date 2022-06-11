package breakbadhabits.android.compose.molecule

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null
) {
    if (progress == null) {
        CircularProgressIndicator(
            modifier = modifier
        )
    } else {
        CircularProgressIndicator(
            modifier = modifier,
            progress = progress
        )
    }
}