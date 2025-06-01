package io.tujh.imago.presentation.screens.stand

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.presentation.base.StateComponent

class StandSelectionScreen : StateComponent<StandSelectionScreen.Action, String> {

    sealed interface Action {
        @JvmInline
        value class Input(val value: String) : Action

        data class Save(val navigator: Navigator) : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: String, onAction: (Action) -> Unit) =
        StandSelectionGreenContent(state, onAction)

    @Composable
    override fun model() = getScreenModel<StandSelectionModel>()
}