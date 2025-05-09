package io.tujh.imago.presentation.screens.post.view

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.post.repository.PostEditor
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.models.PostItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class PostViewModel @AssistedInject constructor(
    @Assisted private val post: PostItem,
    editor: PostEditor.Factory,
    private val errorHandler: ErrorHandler
) : StateModel<PostViewScreen.Action, PostViewScreen.State>,
    StateHolder<PostViewScreen.State> by StateHolder(PostViewScreen.State(post)) {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (PostItem) -> PostViewModel

    private val editor = editor(post.id)
    private val channel = Channel<Unit>(Channel.CONFLATED)

    init {
        observeChannel()
    }

    override fun onAction(action: PostViewScreen.Action) {
        when (action) {
            PostViewScreen.Action.MarkFavorite -> markFavorite()
        }
    }

    private fun observeChannel() {
        screenModelScope.io {
            for (cmd in channel) {
                editor
                    .markFavorite()
                    .onSuccess { inFavorite ->
                        update { it.copy(inFavorite = inFavorite) }
                    }
                    .onFailure {
                        errorHandler("Cannot add to favorites")
                    }
            }
        }
    }

    private fun markFavorite() {
        screenModelScope.launch { channel.send(Unit) }
    }
}