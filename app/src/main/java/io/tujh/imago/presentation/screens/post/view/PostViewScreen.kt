package io.tujh.imago.presentation.screens.post.view

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import cafe.adriel.voyager.hilt.getScreenModel
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.models.PostItem

class PostViewScreen(
    private val post: PostItem,
    private val imageSharedKey: String,
    private val titleSharedKey: String
) : StateComponent<PostViewScreen.Action, PostViewScreen.State> {
    @Immutable
    data class State(
        val post: PostItem,
        val imageSharedKey: String,
        val titleSharedKey: String,
        val pagerState: PagerState = PagerState { post.images.size },
        val inFavorite: Boolean = false
    )

    sealed interface Action {
        data object MarkFavorite : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        PostViewScreenContent(state, onAction)

    @Composable
    override fun model() = getScreenModel<PostViewModel, PostViewModel.Factory> {
        it.create(post, imageSharedKey, titleSharedKey)
    }
}