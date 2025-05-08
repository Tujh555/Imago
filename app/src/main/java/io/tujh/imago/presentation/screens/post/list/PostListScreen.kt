package io.tujh.imago.presentation.screens.post.list

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.lastVisibleItemIndex
import io.tujh.imago.presentation.models.ShortPostItem
import io.tujh.imago.presentation.screens.post.tab.InnerTabComponent

class PostListScreen(
    private val type: PostListType
) : StateComponent<PostListScreen.Action, PostListScreen.State>, InnerTabComponent {
    override val key: ScreenKey = uniqueScreenKey
    override val title: String = type.title

    @Immutable
    data class State(
        val gridState: LazyStaggeredGridState = LazyStaggeredGridState(),
        val posts: List<ShortPostItem> = emptyList(),
        val loadState: LoadState = LoadState.Initial,
        val isRefreshing: Boolean = false,
        val isEmpty: Boolean = false,
    ) {
        val lastVisibleItem = gridState.lastVisibleItemIndex()
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
