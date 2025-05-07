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
    StateHolder<ImageEditScreen.State> by StateHolder(ImageEditScreen.State(sharedKey, bitmap)) {

    @AssistedFactory
    interface Factory : ScreenModelFactory,
            (String, ImageBitmap, @JvmSuppressWildcards (Uri) -> Unit) -> ImageEditScreenModel

    override fun onAction(action: ImageEditScreen.Action) {
        when (action) {
            ImageEditScreen.Action.Save -> save()
            is ImageEditScreen.Action.Update -> updateImage(action.bitmap)
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

    private fun updateImage(bitmap: ImageBitmap) {
        update { it.copy(image = bitmap) }
    }
}