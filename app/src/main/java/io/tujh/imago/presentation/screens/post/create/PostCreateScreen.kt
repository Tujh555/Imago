package io.tujh.imago.presentation.screens.post.create

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.presentation.base.StateComponent

class PostCreateScreen : StateComponent<PostCreateScreen.Action, PostCreateScreen.State> {
    @Immutable
    data class State(
        val photos: List<Uri> = emptyList(),
        val title: String = "",
    ) {
        val canPickCount = (10 - photos.size).coerceAtLeast(0)
    }

    sealed interface Action {
        @JvmInline
        value class Picked(val uri: Uri) : Action

        data class Reorder(val to: Int, val from: Int) : Action

        data class Edit(val uri: Uri, val navigator: Navigator) : Action

        @JvmInline
        value class Title(val value: String) : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        PostCreateScreenContent(state, onAction)

    @Composable
    override fun model(): PostCreateModel = getScreenModel()
}