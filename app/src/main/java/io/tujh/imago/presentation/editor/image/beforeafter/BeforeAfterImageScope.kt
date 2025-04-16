package io.tujh.imago.presentation.editor.image.beforeafter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import io.tujh.imago.presentation.editor.image.ImageScope

interface BeforeAfterImageScope : ImageScope {
    var position: Offset
}

class BeforeAfterImageScopeImpl(
    private val density: Density,
    override val constraints: Constraints,
    override val imageWidth: Dp,
    override val imageHeight: Dp,
    override val rect: IntRect,
    override var position: Offset,
) : BeforeAfterImageScope {

    override val minWidth: Dp get() = with(density) { constraints.minWidth.toDp() }

    override val maxWidth: Dp
        get() = with(density) {
            if (constraints.hasBoundedWidth) constraints.maxWidth.toDp() else Dp.Infinity
        }

    override val minHeight: Dp get() = with(density) { constraints.minHeight.toDp() }

    override val maxHeight: Dp
        get() = with(density) {
            if (constraints.hasBoundedHeight) constraints.maxHeight.toDp() else Dp.Infinity
        }
}
