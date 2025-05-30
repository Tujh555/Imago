package io.tujh.imago.presentation.screens.post.list

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.work.Operation
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.lastVisibleItemIndex
import io.tujh.imago.presentation.models.PostItem
import io.tujh.imago.presentation.screens.post.tab.InnerTabComponent
import kotlinx.coroutines.flow.onStart

class PostListScreen(
    private val type: PostListType
) : StateComponent<PostListScreen.Action, PostListScreen.State>, InnerTabComponent {
    override val key: ScreenKey = uniqueScreenKey
    override val title: String = type.title

    @Immutable
    data class State(
        val type: PostListType,
        val gridState: LazyStaggeredGridState = LazyStaggeredGridState(),
        val posts: List<PostItem> = emptyList(),
        val loadState: LoadState = LoadState.Initial,
        val isRefreshing: Boolean = false,
        val isEmpty: Boolean = false,
    ) {
        val lastVisibleItem = gridState.lastVisibleItemIndex().onStart { emit(0) }
    }

    sealed interface Action {
        data object Refresh : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        PostListScreenContent(state, onAction)

    @Composable
    override fun model() = getScreenModel<PostListModel, PostListModel.Factory> { it(type) }
}
