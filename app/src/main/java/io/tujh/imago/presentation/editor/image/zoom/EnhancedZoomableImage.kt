package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import io.tujh.imago.presentation.editor.image.ImageWithConstraints

@Composable
fun EnhancedZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    limitPan: Boolean = true,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    clip: Boolean = true,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = DefaultOnDoubleTap,
    clipTransformToContentScale: Boolean = false,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .enhancedZoom(
            key1= imageBitmap,
            key2 = contentScale,
            enhancedZoomState = rememberEnhancedZoomState(
                key1 = imageBitmap,
                key2 = contentScale,
                imageSize = IntSize(imageBitmap.width, imageBitmap.height),
                initialZoom = initialZoom,
                minZoom = minZoom,
                maxZoom = maxZoom,
                limitPan = limitPan,
                fling = fling,
                moveToBounds = moveToBounds,
                zoomable = zoomable,
                pannable = pannable,
                rotatable = rotatable
            ),
            clip = clip,
            enabled = enabled,
            zoomOnDoubleTap = zoomOnDoubleTap,
            onGestureStart = onGestureStart,
            onGesture = onGesture,
            onGestureEnd = onGestureEnd
        )

    ImageWithConstraints(
        modifier = if (clipTransformToContentScale) modifier else modifier.then(zoomModifier),
        imageBitmap = imageBitmap,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        drawImage = !clipTransformToContentScale
    ) {

        if (clipTransformToContentScale) {
            Image(
                bitmap = imageBitmap,
                contentScale = contentScale,
                modifier = zoomModifier,
                alignment = alignment,
                contentDescription = null,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
            )
        }
    }
}

@Composable
fun EnhancedZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    clip: Boolean = true,
    clipTransformToContentScale: Boolean = false,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .enhancedZoom(
            enhancedZoomState = enhancedZoomState,
            clip = clip,
            enabled = enabled,
            zoomOnDoubleTap = zoomOnDoubleTap,
            onGestureStart = onGestureStart,
            onGesture = onGesture,
            onGestureEnd = onGestureEnd
        )

    ImageWithConstraints(
        modifier = if (clipTransformToContentScale) modifier else modifier.then(zoomModifier),
        imageBitmap = imageBitmap,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        drawImage = !clipTransformToContentScale
    ) {

        if (clipTransformToContentScale) {
            Image(
                bitmap = imageBitmap,
                contentScale = contentScale,
                modifier = zoomModifier,
                alignment = alignment,
                contentDescription = null,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
            )
        }
    }
}
