package io.tujh.imago.presentation.screens.post.create

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.util.packInts
import androidx.compose.ui.util.unpackInt1
import androidx.compose.ui.util.unpackInt2
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.tujh.imago.presentation.base.StateComponent

class PostCreateScreen : StateComponent<PostCreateScreen.Action, PostCreateScreen.State> {
    @Immutable
    data class State(
        val photos: List<String> = emptyList(),
        val title: String = "",
        val listState: LazyListState = LazyListState(),
    ) {
        val canPickCount = (10 - photos.size).coerceAtLeast(0)
        val createVisible = photos.isNotEmpty()
    }

    sealed interface Action {
        @JvmInline
        value class Picked(val uris: List<Uri>) : Action {
            constructor(uri: Uri): this(listOf(uri))
        }

        @JvmInline
        value class Reorder(private val packed: Long) : Action {
            constructor(to: Int, from: Int): this(packInts(to, from))

            val to get() = unpackInt1(packed)
            val from get() = unpackInt2(packed)
        }

        data class Edit(val key: String, val uri: String, val navigator: Navigator) : Action

        @JvmInline
        value class Remove(val uri: String) : Action

        @JvmInline
        value class Title(val value: String) : Action

        @JvmInline
        value class Create(val navigator: Navigator) : Action
    }

    @Composable
    @NonRestartableComposable
    override fun Content(state: State, onAction: (Action) -> Unit) =
        PostCreateScreenContent(state, onAction)

    @Composable
    override fun model(): PostCreateModel = getScreenModel()
}