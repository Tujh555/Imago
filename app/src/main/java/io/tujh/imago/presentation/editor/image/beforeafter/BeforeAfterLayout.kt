package io.tujh.imago.presentation.editor.image.beforeafter

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize
import io.tujh.imago.presentation.editor.image.beforeafter.AfterLabel
import io.tujh.imago.presentation.editor.image.beforeafter.BeforeLabel
import io.tujh.imago.presentation.editor.image.beforeafter.ContentOrder
import io.tujh.imago.presentation.editor.image.beforeafter.DefaultOverlay
import io.tujh.imago.presentation.editor.image.beforeafter.Layout
import io.tujh.imago.presentation.editor.image.beforeafter.OverlayStyle

@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
) {
    var progress by remember { mutableFloatStateOf(50f) }

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = { progress = it },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = { dpSize: DpSize, offset: Offset ->
            DefaultOverlay(
                width = dpSize.width,
                height = dpSize.height,
                position = offset,
                overlayStyle = overlayStyle
            )
        }
    )
}

@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    overlayStyle: OverlayStyle = OverlayStyle(),
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
) {

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = { dpSize: DpSize, offset: Offset ->
            DefaultOverlay(
                width = dpSize.width,
                height = dpSize.height,
                position = offset,
                overlayStyle = overlayStyle
            )
        }
    )
}

@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {
    var progress by remember { mutableFloatStateOf(50f) }

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = { progress = it },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = overlay
    )
}

@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)? = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable (BoxScope.() -> Unit)? = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {
    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = overlay
    )
}