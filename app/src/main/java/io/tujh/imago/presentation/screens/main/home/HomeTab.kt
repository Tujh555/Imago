package io.tujh.imago.presentation.screens.main.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.StateScreenModel
import io.tujh.imago.R
import io.tujh.imago.presentation.base.Model
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.StateTabComponent
import io.tujh.imago.presentation.base.TabComponent
import io.tujh.imago.presentation.components.BlurredBackground
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object HomeTab : StateTabComponent<HomeTab.Action, HomeTab.State> {
    private fun readResolve(): Any = HomeTab

    @Immutable
    data object State

    sealed interface Action

    override val title: String = "Home"
    override val icon: Int = R.drawable.ic_home


    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        BlurredBackground(Modifier.fillMaxSize()) {  }
    }

    @Composable
    override fun model(): Model<Action, State, Nothing> = remember {
        object : StateModel<Action, State> {
            override val state: StateFlow<State> = MutableStateFlow(State)

            override fun onAction(action: Action) {
            }

        }
    }
}