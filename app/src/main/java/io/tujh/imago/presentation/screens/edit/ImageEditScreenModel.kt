package io.tujh.imago.presentation.screens.edit

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.image.BitmapLoader
import io.tujh.imago.domain.utils.withMinDelay
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.editor.components.EditingComponent
import kotlinx.coroutines.launch

class ImageEditScreenModel @AssistedInject constructor(
    @Assisted private val uri: Uri,
    loader: BitmapLoader.Factory
) : StateModel<ImageEditScreen.Action, ImageEditScreen.State>,
    StateHolder<ImageEditScreen.State> by StateHolder(ImageEditScreen.State()),
    EditingComponent.FinishListener {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (Uri) -> ImageEditScreenModel

    private val loader = loader(uri)

    init {
        loadImage()
    }

    override fun onAction(action: ImageEditScreen.Action) {
        when (action) {
            is ImageEditScreen.Action.SelectComponent -> selectComponent(action.components)
        }
    }

    override fun save(bitmap: ImageBitmap) {
        update { it.copy(image = bitmap, editingComponent = null) }
    }

    override fun close() {
        update { it.copy(editingComponent = null) }
    }

    private fun loadImage() {
        update { it.copy(loadingState = LoadingState.Loading) }
        screenModelScope.launch {
            val bitmap = withMinDelay { loader.load() }
            update {
                if (bitmap == null) {
                    it.copy(loadingState = LoadingState.Error)
                } else {
                    it.copy(loadingState = LoadingState.Success, image = bitmap.asImageBitmap())
                }
            }
        }
    }

    private fun selectComponent(factory: EditFactory) {
        state.value.image?.let { image ->
            val component = factory(image, this)
            update { it.copy(editingComponent = component) }
        }
    }
}