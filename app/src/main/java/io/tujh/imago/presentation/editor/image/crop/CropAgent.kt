package io.tujh.imago.presentation.editor.image.crop

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CropAgent {

    private val imagePaint = Paint().apply {
        blendMode = BlendMode.SrcIn
    }

    private val paint = Paint()


    fun crop(
        imageBitmap: ImageBitmap,
        cropRect: Rect,
        cropOutline: CropOutline,
        layoutDirection: LayoutDirection,
        density: Density,
        rotation: Float = 0f
    ): ImageBitmap {
        val croppedBitmap: Bitmap = Bitmap.createBitmap(
            imageBitmap.asAndroidBitmap(),
            cropRect.left.toInt(),
            cropRect.top.toInt(),
            cropRect.width.toInt(),
            cropRect.height.toInt(),
        )

        val imageToCrop = croppedBitmap
            .copy(Bitmap.Config.ARGB_8888, true)
            .asImageBitmap()

        drawCroppedImage(cropOutline, cropRect, layoutDirection, density, imageToCrop, rotation)

        return imageToCrop
    }

    private fun drawCroppedImage(
        cropOutline: CropOutline,
        cropRect: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
        imageToCrop: ImageBitmap,
        rotation: Float
    ) {
        when (cropOutline) {
            is CropShape -> {

                val path = Path().apply {
                    val outline = cropOutline.shape.createOutline(cropRect.size, layoutDirection, density)
                    addOutline(outline)
                }

                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)
                    rotate(rotation, cropRect.center.x, cropRect.center.y)
                    drawPath(path, paint)
                    drawImage(
                        image = imageToCrop,
                        topLeftOffset = Offset.Zero,
                        paint = imagePaint
                    )
                    restore()
                }
            }
            is CropPath -> {

                val path = Path().apply {
                    addPath(cropOutline.path)

                    val pathSize = getBounds().size
                    val rectSize = cropRect.size

                    val matrix = android.graphics.Matrix()
                    matrix.postScale(
                        rectSize.width / pathSize.width,
                        cropRect.height / pathSize.height
                    )
                    this.asAndroidPath().transform(matrix)

                    val left = getBounds().left
                    val top = getBounds().top

                    translate(Offset(-left, -top))
                }

                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)
                    rotate(rotation, cropRect.center.x, cropRect.center.y)
                    drawPath(path, paint)
                    drawImage(image = imageToCrop, topLeftOffset = Offset.Zero, imagePaint)
                    restore()
                }
            }
        }
    }


    fun resize(
        croppedImageBitmap: ImageBitmap,
        requiredWidth: Int,
        requiredHeight: Int
    ): ImageBitmap {
        val resizedBitmap: Bitmap = Bitmap.createScaledBitmap(
            croppedImageBitmap.asAndroidBitmap(),
            requiredWidth,
            requiredHeight,
            true
        )

        return resizedBitmap.asImageBitmap()
    }
}

