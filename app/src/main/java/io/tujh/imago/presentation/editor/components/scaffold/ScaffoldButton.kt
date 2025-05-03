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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

val alwaysActiveState = mutableStateOf(true)

@Stable
interface ScaffoldButton {
    val active: State<Boolean>
    val source: IconSource

    fun onClick()
}

fun ScaffoldButton(
    active: State<Boolean>,
    source: IconSource,
    onClick: () -> Unit
): ScaffoldButton = object : ScaffoldButton {
    override val active = active
    override val source = source
    override fun onClick() = onClick()
}

fun MutableList<ScaffoldButton>.button(
    @DrawableRes res: Int,
    active: State<Boolean> = alwaysActiveState,
    onClick: () -> Unit
) {
    ScaffoldButton(active, IconSource.Resource(res), onClick).let(::add)
}

fun MutableList<ScaffoldButton>.button(
    vector: ImageVector,
    active: State<Boolean> = alwaysActiveState,
    onClick: () -> Unit
) {
    ScaffoldButton(active, IconSource.Vector(vector), onClick).let(::add)
}

@Composable
fun controlButtons(
    close: () -> Unit,
    save: () -> Unit,
    central: MutableList<ScaffoldButton>.() -> Unit = {}
) = remember {
    buildList {
        button(Icons.Filled.Close, onClick = close)
        central()
        button(Icons.Filled.Done, onClick = save)
    }
}