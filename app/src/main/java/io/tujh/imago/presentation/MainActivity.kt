package io.tujh.imago.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.FadeTransition
import dagger.hilt.android.AndroidEntryPoint
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.screens.post.create.PostCreateScreen
import io.tujh.imago.presentation.screens.post.list.PostListScreen
import io.tujh.imago.presentation.screens.post.tab.PostTabsScreen
import io.tujh.imago.presentation.screens.signin.SignInScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var handler: Handler
    @Inject
    lateinit var localProvider: AppLocalProvider

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            handler.scope = rememberCoroutineScope()
            val view = LocalView.current
            SideEffect {
                val color = Color.White.toArgb()
                window.statusBarColor = color
                window.navigationBarColor = color
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }

            localProvider.WithProviders {
                BottomSheetNavigator(sheetShape = sheetShape) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = {
                            SnackbarHost(
                                hostState = handler.hostState,
                                modifier = Modifier.imePadding()
                            )
                        },
                    ) {
                        // FIXME splash
                        Navigator(PostTabsScreen()) { navigator ->
                            FadeTransition(navigator) {
                                CompositionLocalProvider(
                                    LocalSharedNavVisibilityScope provides this
                                ) {
                                    it.Content()
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