package io.tujh.imago.presentation.screens.signin

import cafe.adriel.voyager.core.model.screenModelScope
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.auth.AuthData
import io.tujh.imago.domain.auth.AuthRepository
import io.tujh.imago.domain.utils.withMinDelay
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.screens.post.list.PostListScreen
import javax.inject.Inject

class SignInModel @Inject constructor(
    private val repository: AuthRepository,
    private val errorHandler: ErrorHandler
) : StateModel<SignInScreen.Action, SignInScreen.State>,
    StateHolder<SignInScreen.State> by StateHolder(SignInScreen.State()) {
    override fun onAction(action: SignInScreen.Action) {
        when (action) {
            is SignInScreen.Action.Email -> update { it.copy(email = action.value) }
            is SignInScreen.Action.Password -> update { it.copy(password = action.value) }
            is SignInScreen.Action.Login -> screenModelScope.io {
                update { it.copy(isLoading = true) }

                val result = withMinDelay {
                    val authData = state.value.run {
                        AuthData(email = email, password = password, name = "")
                    }
                    repository.signUp(authData)
                }

                result
                    .onSuccess { action.navigator.replaceAll(PostListScreen()) }
                    .onFailure {
                        update { it.copy(isLoading = false) }
                        errorHandler("Failed to sign in")
                    }
            }
        }
    }
}