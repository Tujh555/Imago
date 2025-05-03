package io.tujh.imago.presentation.screens.signup

import android.util.Patterns
import androidx.compose.ui.util.fastAny
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.auth.AuthData
import io.tujh.imago.domain.auth.AuthRepository
import io.tujh.imago.domain.auth.buildValidator
import io.tujh.imago.domain.auth.minLength
import io.tujh.imago.domain.auth.notBlank
import io.tujh.imago.domain.auth.pattern
import io.tujh.imago.domain.auth.rule
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.screens.post.list.PostListScreen
import javax.inject.Inject

class SignUpModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val errorHandler: ErrorHandler
) : StateModel<SignUpScreen.Action, SignUpScreen.State>,
    StateHolder<SignUpScreen.State> by StateHolder(SignUpScreen.State()) {

    private val nameValidator = buildValidator {
        notBlank { errorHandler("Name must be non-empty") }
    }
    private val emailValidator = buildValidator {
        pattern(Patterns.EMAIL_ADDRESS.toRegex()) {
            errorHandler("Email is not correct")
        }
    }
    private val passwordValidator = buildValidator {
        minLength(8) { errorHandler("Password length must be at least 8 characters") }
        val specialChars = listOf('!', '@', '$', '%', '^', '-', '_')
        rule({ specialChars.fastAny { it in this } }) {
            val errorText = "Password must contains special characters (${specialChars.joinToString(separator = ", ")})"
            errorHandler(errorText)
        }
    }

    override fun onAction(action: SignUpScreen.Action) {
        when (action) {
            is SignUpScreen.Action.Email -> update {
                it.copy(email = action.value, emailCorrect = true)
            }
            is SignUpScreen.Action.Name -> update {
                it.copy(name = action.value, nameCorrect = true)
            }
            is SignUpScreen.Action.Password -> update {
                it.copy(password = action.value, passwordCorrect = true)
            }
            is SignUpScreen.Action.SignUp -> signUp(action.navigator)
        }
    }

    private fun signUp(navigator: Navigator) {
        if (validateName() && validateEmail() && validatePassword()) {
            screenModelScope.io {
                update { it.copy(isLoading = true) }

                val authData = state.value.run { AuthData(email, password, name) }
                authRepository
                    .signUp(authData)
                    .onSuccess { navigator.replaceAll(PostListScreen()) }
                    .onFailure {
                        update { it.copy(isLoading = false) }
                        errorHandler("Something went wrong")
                    }
            }
        }
    }

    private fun validateName() = nameValidator.validate(state.value.name).also { valid ->
        update { it.copy(nameCorrect = valid) }
    }

    private fun validateEmail() = emailValidator.validate(state.value.email).also { valid ->
        update { it.copy(emailCorrect = valid) }
    }

    private fun validatePassword()  = passwordValidator.validate(state.value.password).also { valid ->
        update { it.copy(passwordCorrect = valid) }
    }
}