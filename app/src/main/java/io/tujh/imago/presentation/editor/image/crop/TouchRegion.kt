package io.tujh.imago.presentation.editor.image.crop

enum class TouchRegion {
    TopLeft, TopRight, BottomLeft, BottomRight, Inside, None
}

fun handlesTouched(touchRegion: TouchRegion) = touchRegion == TouchRegion.TopLeft ||
        touchRegion == TouchRegion.TopRight ||
        touchRegion == TouchRegion.BottomLeft ||
        touchRegion == TouchRegion.BottomRight
