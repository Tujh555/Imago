package io.tujh.imago.presentation.editor.components.scaffold

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector

val alwaysVisibleState = mutableStateOf(true)

@Stable
interface ScaffoldButton {
    val visible: State<Boolean>
    val source: IconSource

    fun onClick()
}

@Immutable
sealed interface IconSource {
    @JvmInline
    value class Resource(@DrawableRes val id: Int) : IconSource

    @JvmInline
    value class Vector(val imageVector: ImageVector) : IconSource
}

fun ScaffoldButton(
    visible: State<Boolean>,
    source: IconSource,
    onClick: () -> Unit
): ScaffoldButton = object : ScaffoldButton {
    override val visible = visible
    override val source = source
    override fun onClick() = onClick()
}

fun MutableList<ScaffoldButton>.button(
    visible: State<Boolean>,
    @DrawableRes res: Int,
    onClick: () -> Unit
) {
    ScaffoldButton(visible, IconSource.Resource(res), onClick).let(::add)
}

fun MutableList<ScaffoldButton>.button(
    visible: State<Boolean>,
    vector: ImageVector,
    onClick: () -> Unit
) {
    ScaffoldButton(visible, IconSource.Vector(vector), onClick).let(::add)
}

@Composable
fun controlButtons(
    close: () -> Unit,
    save: () -> Unit,
    central: MutableList<ScaffoldButton>.() -> Unit = {}
) = remember {
    buildList {
        button(alwaysVisibleState, Icons.Filled.Close, close)
        central()
        button(alwaysVisibleState, Icons.Filled.Done, save)
    }
}