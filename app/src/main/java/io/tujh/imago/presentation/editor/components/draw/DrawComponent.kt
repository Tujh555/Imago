package io.tujh.imago.presentation.editor.components.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.image.ImageWithConstraints
import io.tujh.imago.presentation.editor.image.util.MotionEvent
import io.tujh.imago.presentation.editor.image.util.motionEvents
import kotlinx.coroutines.launch

class DrawComponent(initial: ImageBitmap) : EditingComponent {
    private var width by mutableFloatStateOf(15f)
    private var selectedColor by mutableStateOf(Color.Green)
    private var motionEvent by mutableStateOf<MotionEvent>(MotionEvent.Unspecified)
    private var lastPosition by mutableStateOf(Offset.Unspecified)
    private val paths = mutableStateListOf<DrawingPath>()
    override val key = uniqueScreenKey
    override var state by mutableStateOf(initial)
        private set

    @Composable
    override fun Content() {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val graphicsLayer = rememberGraphicsLayer()
            ImageWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
                imageBitmap = state,
                contentScale = ContentScale.Crop
            ) {
                var drawingPath by remember { mutableStateOf<DrawingPath?>(null) }
                Canvas(
                    modifier = Modifier
                        .size(imageWidth, imageHeight)
                        .clipToBounds()
                        .motionEvents { motionEvent = it }
                ) {
                    when (motionEvent) {
                        is MotionEvent.Down -> {
                            drawingPath = path()
                            drawingPath?.path?.moveTo(motionEvent.x, motionEvent.y)
                            lastPosition = motionEvent.position
                        }

                        is MotionEvent.Move -> {
                            drawingPath?.run {
                                path.quadraticTo(
                                    x1 = lastPosition.x,
                                    y1 = lastPosition.y,
                                    x2 = (lastPosition.x + motionEvent.x) / 2,
                                    y2 = (lastPosition.y + motionEvent.y) / 2
                                )
                                lastPosition = motionEvent.position
                            }
                        }

                        is MotionEvent.Up -> {
                            val path = drawingPath
                            drawingPath = null
                            if (path != null) {
                                path.path.lineTo(motionEvent.x, motionEvent.y)
                                paths.add(path)
                            }
                            motionEvent = MotionEvent.Unspecified
                            lastPosition = motionEvent.position
                        }

                        MotionEvent.Unspecified -> Unit
                    }

                    drawIntoCanvas { canvas ->
                        paths.fastForEach { path ->
                            canvas.drawPath(path.path, path.paint)
                        }
                        drawingPath?.run {
                            canvas.drawPath(path, paint)
                        }
                    }
                }
            }

            val scope = rememberCoroutineScope()
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        state = graphicsLayer.toImageBitmap()
                        paths.clear()
                    }
                },
                content = {
                    Text("Save")
                }
            )
        }
    }

    private fun path() = drawingPath {
        color = selectedColor
        strokeWidth = width
        style = PaintingStyle.Stroke
        isAntiAlias = true
        strokeCap = StrokeCap.Round
    }
}