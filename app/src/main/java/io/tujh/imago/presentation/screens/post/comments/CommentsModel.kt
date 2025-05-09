package io.tujh.imago.presentation.screens.post.comments

import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel

class CommentsModel @AssistedInject constructor(
    @Assisted private val postId: String
) : StateModel<PostCommentsSheet.Action, PostCommentsSheet.State>,
    StateHolder<PostCommentsSheet.State> by StateHolder(PostCommentsSheet.State()) {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (String) -> CommentsModel

    override fun onAction(action: PostCommentsSheet.Action) {

    }
}