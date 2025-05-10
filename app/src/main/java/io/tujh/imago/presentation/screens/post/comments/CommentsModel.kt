package io.tujh.imago.presentation.screens.post.comments

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
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
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.models.CommentItem
import io.tujh.imago.presentation.models.PostItem
import io.tujh.imago.presentation.models.UserItem
import io.tujh.imago.presentation.models.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import kotlin.random.Random.Default.nextInt

class CommentsModel @AssistedInject constructor(
    @Assisted private val post: PostItem,
    @Assisted currentUrl: String,
    getComments: Comments,
    private val errorHandler: ErrorHandler,
) : Model<PostCommentsScreen.Action, PostCommentsScreen.State, PostCommentsScreen.Event>,
    StateHolder<PostCommentsScreen.State> by StateHolder(PostCommentsScreen.State(currentUrl)),
    EventEmitter<PostCommentsScreen.Event> by EventEmitter() {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (PostItem, String) -> CommentsModel

    private val comments = getComments(post.id)
    private val sendingComments = MutableStateFlow<List<CommentItem>>(emptyList())

    init {
        paginate()
    }

    override fun onAction(action: PostCommentsScreen.Action) {
        when (action) {
            is PostCommentsScreen.Action.Comment -> update { it.copy(commentText = action.value) }
            // TODO loader + ptr
            PostCommentsScreen.Action.Refresh -> {
                update { it.copy(isRefreshing = true) }
                screenModelScope.io { comments.refresh() }
            }
            PostCommentsScreen.Action.SendComment -> sendComment()
        }
    }

    private fun sendComment() {
        val newComment = CommentItem(
            id = UUID.randomUUID().toString(),
            author = UserItem(
                id = "123",
                avatar = null,
                name = "You test",
                email = ""
            ),
            createdAt = Instant.now().toString(),
            text = state.value.commentText
        )
        update { it.copy(commentText = "") }
        sendingComments.update {
            it.toMutableList().apply { add(0, newComment) }
        }
        screenModelScope.launch {
            emit(PostCommentsScreen.Event.ScrollTo(0, state.value.lazyListState))
        }
    }

    private fun paginate() {
        comments
            .paginate(state.value.lastVisible)
            .combine(sendingComments) { (elements, loadState), sending ->
                if (loadState == LoadState.Failed) {
                    errorHandler("Something went wrong")
                }

                update {
                    it.copy(
                        comments = sending + elements.fastMap { it.toUi() },
                        loadState = loadState,
                        isRefreshing = false
                    )
                }
            }
            .launchIn(screenModelScope)
    }
}