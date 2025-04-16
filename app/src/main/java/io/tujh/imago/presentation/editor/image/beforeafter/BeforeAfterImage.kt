package io.tujh.imago.presentation.editor.image.beforeafter

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale

@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String? = null,
) {
    var progress by remember { mutableFloatStateOf(50f) }

    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        contentOrder = contentOrder,
        progress = progress,
        onProgressChange = { progress = it },
        contentScale = contentScale,
        alignment = alignment,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        contentDescription = contentDescription,
    ) {
        DefaultOverlay(
            width = imageWidth,
            height = imageHeight,
            position = position,
            overlayStyle = overlayStyle
        )
    }
}

@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String? = null,
) {
    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        contentOrder = contentOrder,
        progress = progress,
        onProgressChange = onProgressChange,
        contentScale = contentScale,
        alignment = alignment,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        contentDescription = contentDescription,
    ) {

        DefaultOverlay(
            width = imageWidth,
            height = imageHeight,
            position = position,
            overlayStyle = overlayStyle
        )
    }
}

@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable BeforeAfterImageScope.() -> Unit
) {

    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        overlay = overlay
    )
}

@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable BeforeAfterImageScope.() -> Unit
) {
    var progress by remember { mutableFloatStateOf(50f) }

    BeforeAfterImageImpl(
        modifier = modifier,
        beforeImage = beforeImage,
        afterImage = afterImage,
        progress = progress,
        onProgressChange = { progress = it },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        overlay = overlay
    )
}
