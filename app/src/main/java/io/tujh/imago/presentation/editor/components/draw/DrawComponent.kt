package io.tujh.imago.presentation.editor.components.draw

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import io.tujh.imago.R
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.draw.brush.DrawBrush
import io.tujh.imago.presentation.editor.components.draw.brush.EraserBrush
import io.tujh.imago.presentation.editor.components.scaffold.EditScaffold
import io.tujh.imago.presentation.editor.components.scaffold.alwaysVisibleState
import io.tujh.imago.presentation.editor.components.scaffold.button
import io.tujh.imago.presentation.editor.components.scaffold.controlButtons
import io.tujh.imago.presentation.editor.image.ImageWithConstraints
import io.tujh.imago.presentation.editor.image.util.MotionEvent
import io.tujh.imago.presentation.editor.image.util.motionEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawComponent(
    private val bitmap: ImageBitmap,
    private val listener: EditingComponent.FinishListener
) : EditingComponent {
    private var width by mutableFloatStateOf(30f)
    private var selectedColor by mutableStateOf(Color.Green)
    private var opacity by mutableFloatStateOf(1f)
    private var motionEvent by mutableStateOf<MotionEvent>(MotionEvent.Unspecified)
    private var lastPosition by mutableStateOf(Offset.Unspecified)
    private val brushes = mutableStateListOf<DrawBrush>()
    private var currentBrush by mutableStateOf<DrawBrush?>(null)
    private var brushFactory by mutableStateOf(BrushFactory.Basic)
    private var latestFactory = BrushFactory.Basic
    override val key = uniqueScreenKey

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val graphicsLayer = rememberGraphicsLayer()
        val undoVisible = rememberUpdatedState(brushes.isNotEmpty())
        val buttons = controlButtons(
            close = listener::close,
            save = {
                scope.launch(Dispatchers.Default) {
                    val edited = graphicsLayer.toImageBitmap()
                    listener.save(edited)
                }
            },
            central = {
                button(undoVisible, R.drawable.ic_arrow_revert) {
                    brushes.removeLastOrNull()
                }
                button(alwaysVisibleState, Icons.Filled.Settings) {

                }
                button(alwaysVisibleState, R.drawable.ic_eraser) {
                    brushFactory = if (brushFactory == BrushFactory.Eraser) {
                        latestFactory
                    } else {
                        latestFactory = brushFactory
                        BrushFactory.Eraser
                    }
                }
            }
        )

        EditScaffold(
            modifier = Modifier.fillMaxSize(),
            buttons = buttons,
        ) {
            with(LocalSharedTransitionScope.current) {
                ImageWithConstraints(
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(
                                key = "editing-image"
                            ),
                            animatedVisibilityScope = LocalSharedNavVisibilityScope.current
                        )
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        },
                    imageBitmap = bitmap,
                    contentScale = ContentScale.Fit,
                ) {
                    Canvas(
                        modifier = Modifier
                            .zIndex(5f)
                            .size(imageWidth, imageHeight)
                            .clipToBounds()
                            .motionEvents { motionEvent = it }
                            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    ) {
                        when (motionEvent) {
                            is MotionEvent.Down -> {
                                if (currentBrush == null) {
                                    currentBrush = brushFactory(
                                        startPosition = motionEvent.position,
                                        size = width,
                                        color = selectedColor,
                                        opacity = opacity
                                    )
                                }
                                lastPosition = motionEvent.position
                            }

                            is MotionEvent.Move -> {
                                currentBrush?.move(lastPosition, motionEvent.position)
                                lastPosition = motionEvent.position
                            }

                            is MotionEvent.Up -> {
                                val brush = currentBrush
                                currentBrush = null
                                if (brush != null) {
                                    brush.dismiss(motionEvent.position)
                                    brushes.add(brush)
                                }
                                motionEvent = MotionEvent.Unspecified
                                lastPosition = motionEvent.position
                            }

                            MotionEvent.Unspecified -> Unit
                        }
                        drawIntoCanvas { canvas ->
                            brushes.fastForEach { brush -> brush.draw(canvas) }
                            currentBrush?.draw(canvas)
                        }
                    }
                }
            }
        }
    }

}