package breakbadhabits.ui.kit

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("ConflictingOnColor")
private val lightColors = lightColors(
    primary = Color(0xFFDF5353),
    primaryVariant = Color(0xFFDF5353),
    secondary = Color(0xFFDF5353),
    onSecondary = Color(0xFF2C2C2C),
    onPrimary = Color(0xFFFFFFFF),
    onError = Color(0xFFF1F1F1),
    error = Color(0xFFff9800),
    background = Color.White
)

@SuppressLint("ConflictingOnColor")
private val darkColors = darkColors(
    primary = Color(0xFFAF4448),
    primaryVariant = Color(0xFFAF4448),
    secondary = Color(0xFFAF4448),
    onSecondary = Color(0xFFF1F1F1),
    onPrimary = Color(0xFFF1F1F1),
    onError = Color(0xFFF1F1F1),
    error = Color(0xFFE48801),
    background = Color.Black
)

@Composable
fun Theme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme.colors
    MaterialTheme(
        colors = if (isDarkTheme) darkColors else lightColors,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(24.dp),
            large = RoundedCornerShape(24.dp),
        )
    ) {
        Surface(
            color = MaterialTheme.colors.background,
            content = content
        )
    }
}