package io.tujh.imago.presentation.screens.post.comments

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import cafe.adriel.voyager.hilt.getScreenModel
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.presentation.base.Component
import io.tujh.imago.presentation.lastVisibleItemIndex
import io.tujh.imago.presentation.models.CommentItem
import io.tujh.imago.presentation.models.PostItem
import java.time.Instant

class PostCommentsScreen(
    private val post: PostItem,
    private val currentUrl: String
) : Component<PostCommentsScreen.Action, PostCommentsScreen.State, PostCommentsScreen.Event> {
    @Immutable
    data class State(
        val currentUrl: String,
        val pof: Instant = Instant.now(),
        val comments: List<CommentItem> = emptyList(),
        val loadState: LoadState = LoadState.Initial,
        val commentText: String = "",
        val lazyListState: LazyListState = LazyListState(),
        val isRefreshing: Boolean = false
    ) {
        val lastVisible = lazyListState.lastVisibleItemIndex()
    }

    sealed interface Action {
        @JvmInline
        value class Comment(val value: String) : Action

        data object SendComment : Action

        data object Refresh : Action
    }

    sealed interface Event {
        class ScrollTo(val index: Int, val listState: LazyListState) : Event
    }

    @Composable
    override fun Event(event: Event) {
        LaunchedEffect(event) {
            when (event) {
                is Event.ScrollTo -> event.listState.animateScrollToItem(event.index)
            }
        }
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        PostCommentSheetContent(state, onAction)

    @Composable
    override fun model() = getScreenModel<CommentsModel, CommentsModel.Factory> {
        it(post, currentUrl)
    }
}