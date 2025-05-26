package io.tujh.imago.presentation.screens.profile

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.work.WorkInfo
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.domain.ErrorHandler
import io.tujh.imago.domain.auth.AuthRepository
import io.tujh.imago.domain.image.BitmapLoader
import io.tujh.imago.domain.user.CurrentUser
import io.tujh.imago.domain.user.ProfileRepository
import io.tujh.imago.domain.utils.withMinDelay
import io.tujh.imago.presentation.base.StateHolder
import io.tujh.imago.presentation.base.StateModel
import io.tujh.imago.presentation.base.io
import io.tujh.imago.presentation.screens.edit.ImageEditScreen
import io.tujh.imago.presentation.screens.signin.SignInScreen
import io.tujh.imago.work.ProfileUpdateWorker
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val currentUser: CurrentUser,
    private val loaderFactory: BitmapLoader.Factory,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : StateModel<ProfileScreen.Action, ProfileScreen.State>,
    StateHolder<ProfileScreen.State> by StateHolder(ProfileScreen.State()) {

    init {
        observeUser()
    }

    override fun onAction(action: ProfileScreen.Action) {
        when (action) {
            is ProfileScreen.Action.Logout -> logout(action.navigator)
            is ProfileScreen.Action.Name -> name(action.value)
            is ProfileScreen.Action.Picked -> picked(action.uri, action.navigator)
            ProfileScreen.Action.Save -> save()
        }
    }

    private fun save() {
        update { it.copy(isLoading = true) }
        screenModelScope.io {
            withMinDelay {
                profileRepository.updateName(state.value.name)
            }

            update { it.copy(isLoading = false) }
        }
    }

    private fun picked(uri: Uri, navigator: Navigator) {
        val loader = loaderFactory(uri)

        screenModelScope.io {
            val bitmap = loader.load()?.asImageBitmap()

            if (bitmap == null) {
                errorHandler("Error loading image")
                return@io
            }

            val screen = ImageEditScreen("profile_avatar", bitmap) { edited ->
                upload(edited)
                navigator.pop()
            }

            navigator.push(screen)
        }
    }

    private fun upload(uri: Uri) {
        update { it.copy(isLoading = true) }
        screenModelScope.launch {
            ProfileUpdateWorker.start(context, uri).collect { info ->
                if (info?.state in terminalStates) {
                    update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun name(value: String) {
        update {
            it.copy(
                name = value,
                finishVisible = value.isNotBlank() && value != currentUser.value?.name
            )
        }
    }

    private fun logout(navigator: Navigator) {
        screenModelScope.io {
            authRepository.logout()
            navigator.replaceAll(SignInScreen())
        }
    }

    private fun observeUser() {
        currentUser
            .filterNotNull()
            .onEach { user ->
                update { it.copy(id = user.id, name = user.name, avatar = user.avatar) }
            }
            .launchIn(screenModelScope)
    }

    companion object {
        private val terminalStates = arrayOf(
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.CANCELLED,
        )
    }
}