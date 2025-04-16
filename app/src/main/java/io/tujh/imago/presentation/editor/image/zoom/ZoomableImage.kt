package io.tujh.imago.presentation.editor.image.zoom

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import io.tujh.imago.presentation.editor.image.ImageWithConstraints

@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    limitPan: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    clip: Boolean = true,
    clipTransformToContentScale: Boolean = false,
    consume: Boolean = true,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .zoom(
            key1 = imageBitmap,
            key2 = contentScale,
            zoomState = rememberZoomState(
                key1 = imageBitmap,
                key2 = contentScale,
                initialZoom = initialZoom,
                minZoom = minZoom,
                maxZoom = maxZoom,
                limitPan = limitPan,
                zoomable = zoomable,
                pannable = pannable,
                rotatable = rotatable,
            ),
            consume = consume,
            clip = clip,
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
fun ZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    clip: Boolean = true,
    clipTransformToContentScale: Boolean = false,
    zoomState: ZoomState,
    consume: Boolean = true,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
) {

    val zoomModifier = Modifier
        .zoom(
            key1 = imageBitmap,
            key2 = contentScale,
            zoomState = zoomState,
            consume = consume,
            clip = clip,
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


