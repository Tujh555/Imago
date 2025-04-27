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
    private val uri: Uri
) : StateComponent<ImageEditScreen.Action, ImageEditScreen.State> {
    sealed interface Action {
        @JvmInline
        value class SelectComponent(val components: EditFactory) : Action

        @JvmInline
        value class OpenFilterComponent(val filter: ShaderFilter) : Action
    }

    @Immutable
    data class State(
        val image: ImageBitmap? = null,
        val loadingState: LoadingState = LoadingState.Loading,
        val editingComponent: EditingComponent? = null
    )

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        ImageEditScreenContent(state, onAction)

    @Composable
    override fun model() =
        getScreenModel<ImageEditScreenModel, ImageEditScreenModel.Factory> { it(uri) }
}