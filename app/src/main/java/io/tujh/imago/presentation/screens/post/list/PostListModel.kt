package io.tujh.imago.presentation.screens.post.list

import androidx.compose.ui.util.fastMap
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.domain.paging.source.PageableSourcePager
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.post.uc.PostList
import io.tujh.imago.domain.utils.withMinDelay
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.models.toUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart

class PostListModel @AssistedInject constructor(
    @Assisted private val type: PostListType,
    private val refreshes: MutableSharedFlow<Set<PostListType>>,
    private val postList: PostList,
    private val errorHandler: ErrorHandler
) : StateModel<PostListScreen.Action, PostListScreen.State>,
    StateHolder<PostListScreen.State> by StateHolder(PostListScreen.State(type)) {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (PostListType) -> PostListModel

    private var latestPager: PageableSourcePager<Post>? = null

    init {
        collectPosts()
    }

    override fun onAction(action: PostListScreen.Action) {
        when (action) {
            PostListScreen.Action.Refresh -> screenModelScope.io {
                update { it.copy(isRefreshing = true) }

                screenModelScope.io {
                    withMinDelay(1000) { latestPager?.refresh() }
                    update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    private fun pager(): PageableSourcePager<Post> {
        val pager = when (type) {
            PostListType.All -> postList.all()
            PostListType.Own -> postList.own()
            PostListType.Favorites -> postList.favorite()
        }
        latestPager = pager
        return pager
    }

    private fun collectPosts() {
        screenModelScope.io {
            refreshes.onStart { emit(setOf(type)) }.filter { type in it }.collectLatest {
                pager().paginate(state.value.lastVisibleItem).collect { (elements, loadState) ->
                    if (loadState == LoadState.Failed) {
                        errorHandler("Something went wrong")
                    }

                    update {
                        it.copy(
                            posts = if (elements.isEmpty() && it.posts.isNotEmpty()) {
                                it.posts
                            } else {
                                elements.fastMap(Post::toUi)
                            },
                            loadState = if (loadState.isLoading() && elements.isEmpty() || it.isRefreshing) {
                                it.loadState
                            } else {
                                loadState
                            },
                            isEmpty = loadState == LoadState.Loaded && elements.isEmpty()
                        )
                    }
                }
            }
        }
    }
}