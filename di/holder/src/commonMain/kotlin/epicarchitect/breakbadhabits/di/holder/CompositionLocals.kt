package epicarchitect.breakbadhabits.di.holder

import androidx.compose.runtime.compositionLocalOf
import epicarchitect.breakbadhabits.di.declaration.AppModule

val LocalAppModule = compositionLocalOf<AppModule> {
    error("LocalAppModule not provided")
}