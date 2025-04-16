package io.tujh.imago.presentation.editor.image.transform

enum class TouchRegion {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight,
    CenterLeft,
    CenterRight,
    TopCenter,
    BottomCenter,
    Inside,
    None
}

enum class HandlePlacement {
    Corner, Side, Both
}