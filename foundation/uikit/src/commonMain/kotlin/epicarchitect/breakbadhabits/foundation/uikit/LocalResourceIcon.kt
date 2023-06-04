package epicarchitect.breakbadhabits.foundation.uikit

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon as MaterialIcon

@Composable
fun LocalResourceIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    MaterialIcon(
        modifier = modifier,
        imageVector = imageVector,
        contentDescription = null,
        tint = tint,
    )
}

@Composable
fun LocalResourceIcon(
    resourceId: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
//    MaterialIcon(
//        modifier = modifier,
//        painter = painterResource(resourceId),
//        contentDescription = null,
//        tint = tint,
//    )
}