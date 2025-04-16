package io.tujh.imago.presentation

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.Navigator
import dagger.hilt.android.AndroidEntryPoint
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.presentation.editor.components.draw.DrawComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
    error("RootNavigator is not provided")
}

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
                val context = LocalContext.current
                var draw by remember { mutableStateOf<DrawComponent?>(null) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    uri?.let {
                        draw = ImageDecoder
                            .createSource(context.contentResolver, it)
                            .let(ImageDecoder::decodeBitmap)
                            .asImageBitmap()
                            .let(::DrawComponent)
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
                    draw?.Content()
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