package io.tujh.imago.presentation.screens.post.create

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import androidx.lifecycle.asFlow
import androidx.work.Operation
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.navigator.Navigator
import coil3.BitmapImage
import coil3.memory.MemoryCache
import coil3.toBitmap
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.domain.image.FullImageLoader
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.screens.edit.ImageEditScreen
import io.tujh.imago.work.PostUploadWorker
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PostCreateModel @AssistedInject constructor(
    @Assisted private val onAdded: @JvmSuppressWildcards (Operation) -> Unit,
    private val imageLoader: FullImageLoader,
    @ApplicationContext private val context: Context
) : StateModel<PostCreateScreen.Action, PostCreateScreen.State>,
    StateHolder<PostCreateScreen.State> by StateHolder(PostCreateScreen.State()) {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (@JvmSuppressWildcards (Operation) -> Unit) -> PostCreateModel

    override fun onAction(action: PostCreateScreen.Action) {
        when (action) {
            is PostCreateScreen.Action.Edit -> edit(action.key, action.uri, action.navigator)
            is PostCreateScreen.Action.Picked -> picked(action.uris)
            is PostCreateScreen.Action.Reorder -> reorder(action.from, action.to)
            is PostCreateScreen.Action.Title -> title(action.value)
            is PostCreateScreen.Action.Remove -> remove(action.uri)
            is PostCreateScreen.Action.Create -> create(action.navigator)
        }
    }

    private fun create(navigator: Navigator) {
        state.value.run {
            PostUploadWorker.start(context, title, photos.map(Uri::parse)).let(onAdded)
        }
        navigator.pop()
    }

    private fun remove(uri: String) = update { it.copy(photos = it.photos - uri) }

    private fun title(value: String) = update { it.copy(title = value) }

    private fun picked(uris: List<Uri>) = update {
        it.copy(photos = it.photos + uris.map(Uri::toString))
    }

    private fun edit(sharedKey: String, uri: String, navigator: Navigator) {
        imageLoader.memoryCache?.let { cache ->
            val bitmapImage = (cache[MemoryCache.Key(uri)]?.image) as? BitmapImage
            bitmapImage?.bitmap?.let { bitmap ->
                val screen = ImageEditScreen(sharedKey, bitmap.asImageBitmap()) { edited ->
                    update {
                        it.copy(
                            photos = it.photos.map { element ->
                                if (element == uri) {
                                    edited.toString()
                                } else {
                                    element
                                }
                            }
                        )
                    }
                    navigator.pop()
                }
                navigator.push(screen)
            }
        }
    }

    private fun reorder(from: Int, to: Int) = update {
        it.copy(photos = it.photos.toMutableList().apply { add(to, removeAt(from)) })
    }
}