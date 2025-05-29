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
import io.tujh.imago.presentation.screens.post.list.isLoading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant

class CommentsModel @AssistedInject constructor(
    @Assisted private val post: PostItem,
    @Assisted currentUrl: String,
    private val getComments: Comments.Factory,
    private val errorHandler: ErrorHandler,
) : Model<PostCommentsScreen.Action, PostCommentsScreen.State, PostCommentsScreen.Event>,
    StateHolder<PostCommentsScreen.State> by StateHolder(PostCommentsScreen.State(currentUrl)),
    EventEmitter<PostCommentsScreen.Event> by EventEmitter() {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (PostItem, String) -> CommentsModel

    private var _comments: Comments? = null

    init {
        paginate()
    }

    override fun onAction(action: PostCommentsScreen.Action) {
        when (action) {
            is PostCommentsScreen.Action.Comment -> update { it.copy(commentText = action.value) }
            PostCommentsScreen.Action.Refresh -> {
                update { it.copy(isRefreshing = true, pof = Instant.now()) }
                screenModelScope.launch {
                    delay(1000)
                    update { it.copy(isRefreshing = false) }
                }
            }
            PostCommentsScreen.Action.SendComment -> sendComment()
        }
    }

    private fun sendComment() {
        val comments = _comments ?: return
        val text = state.value.commentText

        update { it.copy(commentText = "") }

        screenModelScope.io {
            val currentComments = state.value.comments
            launch { comments.send(text) }

            state.map { it.comments }.first { it.size > currentComments.size }
            emit(PostCommentsScreen.Event.ScrollTo(0, state.value.lazyListState))
        }
    }

    private fun paginate() {
        screenModelScope.io {
            state.map { it.pof }.distinctUntilChanged().collectLatest {
                val comments = getComments(post.id).also { _comments = it }
                comments.pager.paginate(state.value.lastVisible).collect { (elements, loadState) ->
                    if (loadState == LoadState.Failed) {
                        errorHandler("Error loading comments")
                    }

                    update {
                        it.copy(
                            comments = if (elements.isEmpty() && it.comments.isNotEmpty()) {
                                it.comments
                            } else {
                                elements.fastMap { it.toUi() }
                            },
                            loadState = if (loadState.isLoading() && elements.isEmpty()) {
                                it.loadState
                            } else {
                                loadState
                            },
                        )
                    }
                }
            }
        }
    }
}