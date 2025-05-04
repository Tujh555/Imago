package io.tujh.imago.presentation.screens.post.create

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import cafe.adriel.voyager.navigator.Navigator
import coil3.BitmapImage
import coil3.memory.MemoryCache
import coil3.toBitmap
import io.tujh.imago.domain.image.FullImageLoader
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.screens.edit.ImageEditScreen
import javax.inject.Inject

class PostCreateModel @Inject constructor(
    private val imageLoader: FullImageLoader
) : StateModel<PostCreateScreen.Action, PostCreateScreen.State>,
    StateHolder<PostCreateScreen.State> by StateHolder(PostCreateScreen.State()) {

    override fun onAction(action: PostCreateScreen.Action) {
        when (action) {
            is PostCreateScreen.Action.Edit -> edit(action.uri, action.navigator)
            is PostCreateScreen.Action.Picked -> picked(action.uris)
            is PostCreateScreen.Action.Reorder -> reorder(action.from, action.to)
            is PostCreateScreen.Action.Title -> title(action.value)
            is PostCreateScreen.Action.Remove -> remove(action.uri)
        }
    }

    private fun remove(uri: String) = update { it.copy(photos = it.photos - uri) }

    private fun title(value: String) = update { it.copy(title = value) }

    private fun picked(uris: List<Uri>) = update {
        it.copy(photos = it.photos + uris.map(Uri::toString))
    }

    private fun edit(uri: String, navigator: Navigator) {
        imageLoader.memoryCache?.let { cache ->
            val bitmapImage = (cache[MemoryCache.Key(uri)]?.image) as? BitmapImage
            bitmapImage?.bitmap?.let { bitmap ->
                val screen = ImageEditScreen(uri, bitmap.asImageBitmap()) { edited ->
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