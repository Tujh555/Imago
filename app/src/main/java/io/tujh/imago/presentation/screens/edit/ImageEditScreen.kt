package io.tujh.imago.presentation.screens.edit

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.hilt.getScreenModel
import io.tujh.imago.presentation.base.StateComponent

class ImageEditScreen(
    private val sharedKey: String,
    private val bitmap: ImageBitmap,
    private val onEdited: (Uri) -> Unit
) : StateComponent<ImageEditScreen.Action, ImageEditScreen.State> {
    sealed interface Action {
        @JvmInline
        value class Update(val bitmap: ImageBitmap) : Action

        data object Save : Action
    }

    @Immutable
    data class State(
        val sharedKey: String,
        val image: ImageBitmap
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