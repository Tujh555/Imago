package io.tujh.imago.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.editor.components.draw.ProvideDrawSettingsManager
import io.tujh.imago.presentation.locals.DrawSettingsManagerImpl
import io.tujh.imago.presentation.theme.colors.ImagoTheme
import javax.inject.Inject

class AppLocalProvider @Inject constructor(
    private val drawSettingsManagerImpl: DrawSettingsManagerImpl
) {
    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun WithProviders(content: @Composable () -> Unit) {
        ImagoTheme {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    ProvideDrawSettingsManager(drawSettingsManagerImpl, content)
                }
            }
        }
    }
}