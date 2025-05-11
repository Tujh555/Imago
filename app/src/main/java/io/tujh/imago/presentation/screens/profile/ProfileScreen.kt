package io.tujh.imago.presentation.screens.profile

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.presentation.base.Model
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.components.BlurredBackground

class ProfileScreen : StateComponent<ProfileScreen.Action, ProfileScreen.State> {
    @Immutable
    data class State(
        val id: String = "",
        val avatar: String? = null,
        val name: String = "",
        val finishVisible: Boolean = false
    )

    sealed interface Action {
        @JvmInline
        value class Name(val value: String) : Action

        data class Picked(val uri: Uri, val navigator: Navigator) : Action

        data object Save : Action

        data class Logout(val navigator: Navigator) : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        ProfileScreenContent(state, onAction)

    @Composable
    override fun model(): ProfileModel = getScreenModel()
}