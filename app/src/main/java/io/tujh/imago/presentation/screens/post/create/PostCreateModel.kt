package io.tujh.imago.presentation.screens.post.create

import android.net.Uri
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.screens.edit.ImageEditScreen
import javax.inject.Inject

class PostCreateModel @Inject constructor(

) : StateModel<PostCreateScreen.Action, PostCreateScreen.State>,
    StateHolder<PostCreateScreen.State> by StateHolder(PostCreateScreen.State()) {

    override fun onAction(action: PostCreateScreen.Action) {
        when (action) {
            is PostCreateScreen.Action.Edit -> edit(action.uri, action.navigator)
            is PostCreateScreen.Action.Picked -> picked(action.uri)
            is PostCreateScreen.Action.Reorder -> reorder(action.from, action.to)
            is PostCreateScreen.Action.Title -> title(action.value)
        }
    }

    private fun title(value: String) = update { it.copy(title = value) }

    private fun picked(uri: Uri) = update { it.copy(photos = it.photos + uri) }

    private fun edit(uri: Uri, navigator: Navigator) {
        navigator.push(ImageEditScreen(uri))
    }

    private fun reorder(from: Int, to: Int) = update {
        it.copy(photos = it.photos.toMutableList().apply { add(to, removeAt(from)) })
    }
}