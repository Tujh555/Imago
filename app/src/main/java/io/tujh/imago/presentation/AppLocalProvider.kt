package io.tujh.imago.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.tujh.imago.domain.image.FullImageLoader
import io.tujh.imago.domain.image.WriteableUriProvider
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.editor.components.draw.LocalDrawSettingsManager
import io.tujh.imago.presentation.locals.DrawSettingsManagerImpl
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.locals.LocalUriProvider
import io.tujh.imago.presentation.theme.colors.ImagoTheme
import javax.inject.Inject

class AppLocalProvider @Inject constructor(
    private val drawSettingsManagerImpl: DrawSettingsManagerImpl,
    private val fullImageLoader: FullImageLoader,
    private val uriProvider: WriteableUriProvider
) {
    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun WithProviders(content: @Composable () -> Unit) {
        ImagoTheme {
            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this,
                    LocalDrawSettingsManager provides drawSettingsManagerImpl,
                    LocalFullImageLoader provides fullImageLoader,
                    LocalUriProvider provides uriProvider,
                    content = content
                )
            }
        }
    }
}