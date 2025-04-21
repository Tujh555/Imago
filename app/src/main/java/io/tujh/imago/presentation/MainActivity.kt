@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.tujh.imago.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import dagger.hilt.android.AndroidEntryPoint
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.screens.edit.ImageEditScreen
import io.tujh.imago.presentation.screens.splash.SplashScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var handler: Handler

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            handler.scope = rememberCoroutineScope()
            DisposableEffect(Unit) {
                onDispose { handler.scope = null }
            }
            MaterialTheme {
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        Navigator(SplashScreen()) { navigator ->
                            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                                if (uri != null) {
                                    navigator.push(ImageEditScreen(uri))
                                }
                            }

                            Scaffold(
                                modifier = Modifier.systemBarsPadding(),
                                snackbarHost = {
                                    SnackbarHost(
                                        hostState = handler.hostState,
                                        modifier = Modifier.imePadding()
                                    )
                                },
                                floatingActionButton = {
                                    FloatingActionButton(
                                        onClick = {
                                            launcher.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                                    }
                                }
                            ) {
                                CurrentScreen()
//                        SharedTransitionLayout {
//                            val sharedTransitionScope = this
//                            ScreenTransition(
//                                navigator = navigator,
//                                transition = {
//                                    val spec = spring<Float>(stiffness = Spring.StiffnessMediumLow)
//                                    fadeIn(spec) togetherWith fadeOut(spec)
//                                },
//                                content = {
//                                    CompositionLocalProvider(
//                                        LocalSharedTransitionScope provides sharedTransitionScope,
//                                        LocalSharedNavVisibilityScope provides this
//                                    ) {
//                                        it.Content()
//                                    }
//                                }
//                            )
//                        }
                            }
                        }
                    }
                }
            }
        }
    }

    @Stable
    @Singleton
    class Handler @Inject constructor() : ErrorHandler {
        var scope: CoroutineScope? = null
        val hostState = SnackbarHostState()

        override fun invoke(p1: String) {
            scope?.launch {
                hostState.showSnackbar(message = p1, withDismissAction = true)
            }
        }
    }
}