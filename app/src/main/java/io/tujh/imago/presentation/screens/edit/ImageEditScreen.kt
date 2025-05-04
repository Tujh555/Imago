package io.tujh.imago.presentation.screens.edit

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.hilt.getScreenModel
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.filters.shader.ShaderFilter

class ImageEditScreen(
    private val sharedKey: String,
    private val bitmap: ImageBitmap,
    private val onEdited: (Uri) -> Unit
) : StateComponent<ImageEditScreen.Action, ImageEditScreen.State> {
    sealed interface Action {
        @JvmInline
        value class SelectComponent(val components: EditFactory) : Action

        @JvmInline
        value class OpenFilterComponent(val filter: ShaderFilter) : Action

        data object Save : Action
    }

    @Immutable
    data class State(
        val sharedKey: String,
        val image: ImageBitmap,
        val editingComponent: EditingComponent? = null
    )

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        ImageEditScreenContent(state, onAction)

    @Composable
    override fun model() = getScreenModel<ImageEditScreenModel, ImageEditScreenModel.Factory> {
        it(sharedKey, bitmap, onEdited)
    }
}