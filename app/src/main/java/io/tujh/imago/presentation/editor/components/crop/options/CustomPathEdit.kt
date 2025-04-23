package io.tujh.imago.presentation.editor.components.crop.options

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import io.tujh.imago.presentation.editor.image.crop.AspectRatio
import io.tujh.imago.presentation.editor.image.crop.CustomPathOutline
import io.tujh.imago.presentation.editor.image.util.calculateSizeAndOffsetFromAspectRatio
import io.tujh.imago.presentation.editor.image.util.drawBlockWithCheckerAndLayer
import io.tujh.imago.presentation.editor.image.util.scaleAndTranslatePath

@Composable
fun CustomPathEdit(
    aspectRatio: AspectRatio,
    dstBitmap: ImageBitmap,
    customPathOutline: CustomPathOutline,
    onChange: (CustomPathOutline) -> Unit
) {
    var newTitle by remember {
        mutableStateOf(customPathOutline.title)
    }

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .aspectRatio(4 / 3f)
                .drawWithCache {

                    val path = Path().apply {
                        addPath(customPathOutline.path)

                        val (newSize, offset) = calculateSizeAndOffsetFromAspectRatio(
                            aspectRatio = aspectRatio,
                            coefficient = 1f,
                            size = size
                        )
                        scaleAndTranslatePath(newSize.width, newSize.height)
                        translate(offset)
                    }

                    onDrawWithContent {
                        drawBlockWithCheckerAndLayer(dstBitmap) {
                            drawPath(path, Color.Red)

                        }
                    }
                }

        )

        CropTextField(
            value = newTitle,
            onValueChange = {
                newTitle = it
                onChange(
                    customPathOutline.copy(
                        title = newTitle
                    )
                )

            }
        )
    }
}
