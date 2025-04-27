package io.tujh.imago.presentation.screens.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.presentation.base.StateComponent

class SignUpScreen : StateComponent<SignUpScreen.Action, SignUpScreen.State> {
    @Immutable
    data class State(
        val email: String = "",
        val password: String = "",
        val name: String = "",
        val isLoading: Boolean = false,
        val nameCorrect: Boolean = true,
        val passwordCorrect: Boolean = true,
        val emailCorrect: Boolean = true
    )

    sealed interface Action {
        @JvmInline
        value class Email(val value: String) : Action
        @JvmInline
        value class Password(val value: String) : Action
        @JvmInline
        value class Name(val value: String) : Action
        @JvmInline
        value class SignUp(val navigator: Navigator) : Action
    }

    @Composable
    override fun model(): SignUpModel = getScreenModel()

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        SignUpScreenContent(state, onAction)
}