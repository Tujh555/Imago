package io.tujh.imago.presentation.editor.components

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import io.tujh.imago.presentation.base.sub.SubComponent

@Stable
interface EditingComponent : Screen {
    @Stable
    interface FinishListener {
        fun save(bitmap: ImageBitmap)

        fun close()
    }
}