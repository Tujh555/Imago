package io.tujh.imago.presentation.screens.edit

import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.image.BitmapLoader
import io.tujh.imago.domain.image.SaveBitmap
import io.tujh.imago.domain.utils.withMinDelay
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.editor.components.EditingComponent
import io.tujh.imago.presentation.editor.components.filters.FiltersComponent
import kotlinx.coroutines.launch

class ImageEditScreenModel @AssistedInject constructor(
    @Assisted private val sharedKey: String,
    @Assisted private val bitmap: ImageBitmap,
    @Assisted private val onEdited: @JvmSuppressWildcards (Uri) -> Unit,
    private val saveBitmap: SaveBitmap
) : StateModel<ImageEditScreen.Action, ImageEditScreen.State>,
    StateHolder<ImageEditScreen.State> by StateHolder(ImageEditScreen.State(sharedKey, bitmap)),
    EditingComponent.FinishListener {

    @AssistedFactory
    interface Factory : ScreenModelFactory,
            (String, ImageBitmap, @JvmSuppressWildcards (Uri) -> Unit) -> ImageEditScreenModel

    override fun onAction(action: ImageEditScreen.Action) {
        when (action) {
            is ImageEditScreen.Action.SelectComponent -> selectComponent(action.components)
            is ImageEditScreen.Action.OpenFilterComponent -> {
                state.value.image.let { image ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val component = FiltersComponent(action.filter, image, this)
                        update { it.copy(editingComponent = component) }
                    }
                }
            }

            ImageEditScreen.Action.Save -> save()
        }
    }

    private fun save() {
        // TODO лоадер
        screenModelScope.io {
            val bitmap = state.value.image.asAndroidBitmap()
            val savedUri = saveBitmap(bitmap)
            onEdited(savedUri)
        }
    }

    override fun save(bitmap: ImageBitmap) {
        update { it.copy(image = bitmap, editingComponent = null) }
    }

    override fun close() {
        update { it.copy(editingComponent = null) }
    }

    private fun selectComponent(factory: EditFactory) {
        state.value.image.let { image ->
            val component = factory(image, this)
            update { it.copy(editingComponent = component) }
        }
    }
}