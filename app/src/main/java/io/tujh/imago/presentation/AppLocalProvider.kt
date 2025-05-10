@file:OptIn(ExperimentalVoyagerApi::class)

package io.tujh.imago.presentation

import androidx.collection.MutableScatterMap
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.Saver
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigatorSaver
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorSaver
import io.tujh.imago.domain.image.FullImageLoader
import io.tujh.imago.domain.image.WriteableUriProvider
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.editor.components.draw.LocalDrawSettingsManager
import io.tujh.imago.presentation.locals.DrawSettingsManagerImpl
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.locals.LocalUriProvider
import io.tujh.imago.presentation.theme.colors.ImagoTheme
import java.util.UUID
import javax.inject.Inject

class AppLocalProvider @Inject constructor(
    private val drawSettingsManagerImpl: DrawSettingsManagerImpl,
    private val fullImageLoader: FullImageLoader,
    private val uriProvider: WriteableUriProvider
) {
    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalVoyagerApi::class)
    @Composable
    fun WithProviders(content: @Composable () -> Unit) {
        ImagoTheme {
            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this,
                    LocalDrawSettingsManager provides drawSettingsManagerImpl,
                    LocalFullImageLoader provides fullImageLoader,
                    LocalUriProvider provides uriProvider,
                    LocalNavigatorSaver provides RamNavigatorSaver.saver,
                    content = content
                )
            }
        }
    }

    private object RamNavigatorSaver {
        private val saverMap = MutableScatterMap<String, List<Screen>>()
        @OptIn(InternalVoyagerApi::class)
        val saver = NavigatorSaver { initialScreens, key, stateHolder, disposeBehavior, parent ->
            Saver(
                save = {
                    val uuid = UUID.randomUUID().toString()
                    saverMap[uuid] = it.items
                    uuid
                },
                restore = { uuid ->
                    val items = saverMap[uuid]
                    saverMap.remove(uuid)
                    Navigator(items ?: initialScreens, key, stateHolder, disposeBehavior, parent)
                }
            )
        }
    }
}