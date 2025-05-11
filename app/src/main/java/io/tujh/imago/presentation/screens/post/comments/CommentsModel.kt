package io.tujh.imago.presentation.screens.post.comments

import androidx.compose.ui.util.fastMap
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.domain.post.uc.Comments
import io.tujh.imago.presentation.base.EventEmitter
import io.tujh.imago.presentation.base.Model
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.models.PostItem
import io.tujh.imago.presentation.models.toUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CommentsModel @AssistedInject constructor(
    @Assisted private val post: PostItem,
    @Assisted currentUrl: String,
    getComments: Comments.Factory,
    private val errorHandler: ErrorHandler,
) : Model<PostCommentsScreen.Action, PostCommentsScreen.State, PostCommentsScreen.Event>,
    StateHolder<PostCommentsScreen.State> by StateHolder(PostCommentsScreen.State(currentUrl)),
    EventEmitter<PostCommentsScreen.Event> by EventEmitter() {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (PostItem, String) -> CommentsModel

    private val comments = getComments(post.id)

    init {
        paginate()
    }

    override fun onAction(action: PostCommentsScreen.Action) {
        when (action) {
            is PostCommentsScreen.Action.Comment -> update { it.copy(commentText = action.value) }
            PostCommentsScreen.Action.Refresh -> {
                update { it.copy(isRefreshing = true) }
                screenModelScope.io { comments.pager.refresh() }
            }
            PostCommentsScreen.Action.SendComment -> sendComment()
        }
    }

    private fun sendComment() {
        val text = state.value.commentText

        update { it.copy(commentText = "") }

        screenModelScope.io {
            comments.send(text) {
                launch {
                    emit(PostCommentsScreen.Event.ScrollTo(0, state.value.lazyListState))
                }
            }
        }
    }

    private fun paginate() {
        comments.pager
            .paginate(state.value.lastVisible)
            .onEach { (elements, loadState) ->
                if (loadState == LoadState.Failed) {
                    errorHandler("Something went wrong")
                }

                update {
                    it.copy(
                        comments = elements.fastMap { it.toUi() },
                        loadState = loadState,
                        isRefreshing = false
                    )
                }
            }
            .launchIn(screenModelScope)
    }
}