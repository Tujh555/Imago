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
import io.tujh.imago.domain.user.CurrentUser
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.models.PostItem
import io.tujh.imago.presentation.models.toUi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CommentsModel @AssistedInject constructor(
    @Assisted private val post: PostItem,
    @Assisted currentUrl: String,
    getComments: Comments,
    private val errorHandler: ErrorHandler,
    private val currentUser: CurrentUser
) : StateModel<PostCommentsScreen.Action, PostCommentsScreen.State>,
    StateHolder<PostCommentsScreen.State> by StateHolder(PostCommentsScreen.State(currentUrl)) {

    @AssistedFactory
    interface Factory : ScreenModelFactory, (PostItem, String) -> CommentsModel

    private val comments = getComments(post.id)
    private val user = screenModelScope.async { currentUser.filterNotNull().first() }

    init {
        paginate()
    }

    override fun onAction(action: PostCommentsScreen.Action) {

    }

    private fun paginate() {
        comments
            .paginate(state.value.lastVisible)
            .onEach { (elements, loadState) ->
                if (loadState == LoadState.Failed) {
                    errorHandler("Something went wrong")
                }
                val user = user.await()

                update {
                    it.copy(
                        comments = elements.fastMap { it.toUi(user) },
                        loadState = loadState,
                        isRefreshing = false
                    )
                }
            }
            .launchIn(screenModelScope)
    }
}