package io.tujh.imago.presentation.editor.components.filters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.filters.shader.ShaderFilter
import io.tujh.imago.presentation.editor.components.scaffold.EditScaffold
import io.tujh.imago.presentation.editor.components.scaffold.controlButtons
import io.tujh.imago.presentation.editor.image.ImageWithConstraints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FiltersComponent(
    private val sharedKey: String,
    private val filter: ShaderFilter,
    private val bitmap: ImageBitmap,
    private val saver: EditingComponent.Saver
) : EditingComponent {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val graphicsLayer = rememberGraphicsLayer()
        val navigator = LocalNavigator.currentOrThrow
        val buttons = controlButtons(
            close = navigator::pop,
            save = {
                scope.launch(Dispatchers.Default) {
                    val edited = graphicsLayer.toImageBitmap()
                    saver(edited)
                }
            },
        )
        Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
            EditScaffold(
                modifier = Modifier.fillMaxWidth().weight(1f),
                buttons = buttons,
            ) {
                ImageWithConstraints(
                    modifier = LocalSharedTransitionScope.current.run {
                        Modifier
                            .sharedElement(
                                state = rememberSharedContentState(
                                    key = sharedKey
                                ),
                                animatedVisibilityScope = LocalSharedNavVisibilityScope.current
                            )
                            .drawWithContent {
                                graphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(graphicsLayer)
                            }
                    },
                    imageBitmap = bitmap,
                    contentScale = ContentScale.Fit,
                    drawImage = false
                ) {
                    Image(
                        modifier = Modifier
                            .size(imageWidth, imageHeight)
                            .clipToBounds()
                            .onSizeChanged { filter.onSizeChanged(it) }
                            .graphicsLayer { filter.apply() },
                        bitmap = bitmap,
                        contentScale = ContentScale.Fit,
                        contentDescription = null
                    )
                }
            }

            with(LocalSharedNavVisibilityScope.current) {
                Box(
                    modifier = Modifier.animateEnterExit(
                        enter = fadeIn(),
                        exit = fadeOut(),
                    )
                ) {
                    filter.Controls()
                }
            }
        }
    }
}