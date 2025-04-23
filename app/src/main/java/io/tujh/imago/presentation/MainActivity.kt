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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.screens.edit.ImageEditScreen
import io.tujh.imago.presentation.screens.splash.SplashScreen
import io.tujh.imago.presentation.theme.colors.ImagoTheme
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
            ImagoTheme {
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        BottomSheetNavigator(
                            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ) {
                            Navigator(SplashScreen()) { navigator ->
                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.PickVisualMedia()
                                ) { uri ->
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
                                                ActivityResultContracts
                                                    .PickVisualMedia
                                                    .ImageOnly
                                                    .let(::PickVisualMediaRequest)
                                                    .let(launcher::launch)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                ) {
                                    CurrentScreen()
                                }
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