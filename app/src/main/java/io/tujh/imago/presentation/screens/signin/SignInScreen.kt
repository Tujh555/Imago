package io.tujh.imago.presentation.screens.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.presentation.base.StateComponent

class SignInScreen : StateComponent<SignInScreen.Action, SignInScreen.State> {
    @Immutable
    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
    )

    @Immutable
    sealed interface Action {
        @JvmInline
        value class Email(val value: String) : Action
        @JvmInline
        value class Password(val value: String) : Action
        @JvmInline
        value class Login(val navigator: Navigator) : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        SignInScreenContent(state, onAction)

    @Composable
    override fun model(): SignInModel = getScreenModel()
}