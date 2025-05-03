package io.tujh.imago.presentation.editor.components.scaffold

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

@Immutable
sealed interface IconSource {
    @JvmInline
    value class Resource(@DrawableRes val id: Int) : IconSource

    @JvmInline
    value class Vector(val imageVector: ImageVector) : IconSource
}

@Composable
fun IconSource.painter() = when (this) {
    is IconSource.Resource -> painterResource(id)
    is IconSource.Vector -> rememberVectorPainter(imageVector)
}

fun Int.asSource() = IconSource.Resource(this)

fun ImageVector.asSource() = IconSource.Vector(this)