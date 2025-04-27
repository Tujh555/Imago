package io.tujh.imago.presentation.editor.components.draw

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import io.tujh.imago.R
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.draw.brush.DrawBrush
import io.tujh.imago.presentation.editor.components.scaffold.EditScaffold
import io.tujh.imago.presentation.editor.components.scaffold.button
import io.tujh.imago.presentation.editor.components.scaffold.controlButtons
import io.tujh.imago.presentation.editor.image.ImageWithConstraints
import io.tujh.imago.presentation.editor.image.util.MotionEvent
import io.tujh.imago.presentation.editor.image.util.motionEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class DrawComponent(
    private val bitmap: ImageBitmap,
    private val listener: EditingComponent.FinishListener
) : EditingComponent {
    private var motionEvent by mutableStateOf<MotionEvent>(MotionEvent.Unspecified)
    private var lastPosition by mutableStateOf(Offset.Unspecified)
    private val brushes = mutableStateListOf<DrawBrush>()
    private var currentBrush by mutableStateOf<DrawBrush?>(null)
    override val key = uniqueScreenKey

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val graphicsLayer = rememberGraphicsLayer()
        val undoActive = rememberUpdatedState(brushes.isNotEmpty())
        val settingsManager = LocalDrawSettingsManager.current
        var settings by remember { mutableStateOf(DrawSettings.default) }
        val sheetNavigator = LocalBottomSheetNavigator.current
        var brushFactory by remember { mutableStateOf(settings.brushFactory) }
        val eraserActive = remember { mutableStateOf(false) }

        DisposableEffect(Unit) {
            onDispose { settingsManager.update(settings) }
        }

        LaunchedEffect(Unit) {
            settingsManager.settings.distinctUntilChanged().collect { saved ->
                settings = saved
            }
        }

        LaunchedEffect(Unit) {
            snapshotFlow { settings.brushFactory }
                .distinctUntilChanged()
                .collect { brushFactory = it }
        }

        val buttons = controlButtons(
            close = listener::close,
            save = {
                scope.launch(Dispatchers.Default) {
                    val edited = graphicsLayer.toImageBitmap()
                    listener.save(edited)
                }
            },
            central = {
                button(R.drawable.ic_arrow_revert, undoActive) {
                    brushes.removeLastOrNull()
                }
                button(Icons.Filled.Settings) {
                    sheetNavigator.show(DrawSettingsSheet(settings) { settings = it })
                }
                button(R.drawable.ic_eraser, eraserActive) {
                    brushFactory = if (brushFactory == BrushFactory.Eraser) {
                        eraserActive.value = false
                        settings.brushFactory
                    } else {
                        eraserActive.value = true
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
                                        size = settings.size,
                                        color = settings.selectedColor,
                                        opacity = settings.opacity
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