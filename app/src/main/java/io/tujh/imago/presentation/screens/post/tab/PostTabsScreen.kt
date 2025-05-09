package io.tujh.imago.presentation.screens.post.tab

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.work.Operation
import cafe.adriel.voyager.hilt.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.screens.post.list.PostListScreen
import io.tujh.imago.presentation.screens.post.list.PostListType

class PostTabsScreen : StateComponent<PostTabsScreen.Action, PostTabsScreen.State> {
    @Immutable
    data class State(
        val tabs: List<InnerTabComponent> = PostListType.entries.map(::PostListScreen),
        val pagerState: PagerState = PagerState { tabs.size }
    )

    sealed interface Action {
        data class OnAdded(val operation: Operation): Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        PostTabsScreenContent(state, onAction)

    @Composable
    override fun model(): PostTabsModel = LocalNavigator.currentOrThrow.getNavigatorScreenModel()
}