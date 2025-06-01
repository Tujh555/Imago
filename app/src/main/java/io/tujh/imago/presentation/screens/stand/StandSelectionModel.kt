package io.tujh.imago.presentation.screens.stand

import android.util.Log
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.domain.ip.Stand
import io.tujh.imago.domain.ip.StandRepository
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StandSelectionModel @Inject constructor(
    private val repository: StandRepository
) : StateModel<StandSelectionScreen.Action, String>,
    StateHolder<String> by StateHolder("") {

    init {
        repository.current
            .filterNotNull()
            .onEach { stand -> update { stand.value } }
            .launchIn(screenModelScope)
    }

    override fun onAction(action: StandSelectionScreen.Action) {
        when (action) {
            is StandSelectionScreen.Action.Input -> update { action.value }
            is StandSelectionScreen.Action.Save -> save(action.navigator)
        }
    }

    private fun save(navigator: Navigator) {
        screenModelScope.io {
            Log.e("--tag", "stand = ${state.value}")
            repository.update(state.value.let(::Stand))
            navigator.pop()
        }
    }
}