package io.tujh.imago.presentation.screens.post.tab

import androidx.lifecycle.asFlow
import androidx.work.Operation
import cafe.adriel.voyager.core.model.screenModelScope
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostTabsModel @Inject constructor() :
    StateModel<PostTabsScreen.Action, PostTabsScreen.State>,
    StateHolder<PostTabsScreen.State> by StateHolder(PostTabsScreen.State()) {

    private var refreshJob: Job? = null

    override fun onAction(action: PostTabsScreen.Action) {

        when (action) {
            is PostTabsScreen.Action.OnAdded -> {
                refreshJob?.cancel()
                refreshJob = screenModelScope.launch {
                    action.operation.state.asFlow().collect { operationState ->
                        val isSuccess = operationState is Operation.State.SUCCESS
                        val isActive = currentCoroutineContext().isActive
                        if (isSuccess && isActive) {
                            update { PostTabsScreen.State() }
                        }
                    }
                }
            }
        }
    }
}