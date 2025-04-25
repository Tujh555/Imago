package io.tujh.imago.presentation.editor.components.draw

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.Flow

@Stable
interface DrawSettingsManager {
    val settings: Flow<DrawSettings>

    fun update(settings: DrawSettings)
}

val LocalDrawSettingsManager = staticCompositionLocalOf<DrawSettingsManager> {
    error("DrawSettingsManager is not provided")
}

@Composable
fun ProvideDrawSettingsManager(
    manager: DrawSettingsManager,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalDrawSettingsManager provides manager, content)
}