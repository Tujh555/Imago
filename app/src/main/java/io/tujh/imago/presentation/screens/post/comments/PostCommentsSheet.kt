package io.tujh.imago.presentation.screens.post.comments

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.presentation.base.Model
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.models.CommentItem

class PostCommentsSheet(
    private val postId: String
) : StateComponent<PostCommentsSheet.Action, PostCommentsSheet.State> {
    @Immutable
    data class State(
        val comments: List<CommentItem> = emptyList(),
        val loadingState: LoadState = LoadState.Initial,
        val text: String = "",
        val lazyListState: LazyListState = LazyListState()
    )

    sealed interface Action {
        @JvmInline
        value class Comment(val value: String) : Action

        data object SendComment : Action

        data object Refresh : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        PostCommentSheetContent(state, onAction)

    @Composable
    override fun model(): Model<Action, State, Nothing> {
        TODO("Not yet implemented")
    }
}