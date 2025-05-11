package io.tujh.imago.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.R
import io.tujh.imago.presentation.components.BlurredBackground
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.editor.components.scaffold.asSource

@Composable
fun ProfileScreenContent(state: ProfileScreen.State, onAction: (ProfileScreen.Action) -> Unit) {
    BlurredBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    iconSource = Icons.AutoMirrored.Filled.KeyboardArrowLeft.asSource(),
                    onClick = { navigator.pop() }
                )
                IconButton(
                    iconSource = R.drawable.ic_logout.asSource(),
                    onClick = { onAction(ProfileScreen.Action.Logout(navigator)) }
                )
            }
        }
    }
}