package io.tujh.imago.presentation.screens.post.list

import androidx.compose.ui.util.fastMap
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.post.uc.PostList
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.models.toUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PostListModel @AssistedInject constructor(
    @Assisted private val type: PostListType,
    postList: PostList,
    private val errorHandler: ErrorHandler
) : StateModel<PostListScreen.Action, PostListScreen.State>,
    StateHolder<PostListScreen.State> by StateHolder(PostListScreen.State()) {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (PostListType) -> PostListModel

    private val pager = when (type) {
        PostListType.All -> postList.all()
        PostListType.Own -> postList.own()
        PostListType.Favorites -> postList.favorite()
    }

    init {
        collectPosts()
    }

    override fun onAction(action: PostListScreen.Action) {
        when (action) {
            PostListScreen.Action.Refresh -> screenModelScope.io {
                update { it.copy(isRefreshing = true) }
                pager.refresh()
            }
        }
    }

    private fun collectPosts() {
        pager
            .paginate(state.value.lastVisibleItem)
            .onEach { (elements, loadState) ->
                if (loadState == LoadState.Failed) {
                    errorHandler("Something went wrong")
                }
                update {
                    it.copy(
                        posts = elements.fastMap(Post::toUi),
                        loadState = loadState,
                        isRefreshing = false,
                        isEmpty = loadState == LoadState.Loaded && elements.isEmpty()
                    )
                }
            }
            .launchIn(screenModelScope)
    }
}