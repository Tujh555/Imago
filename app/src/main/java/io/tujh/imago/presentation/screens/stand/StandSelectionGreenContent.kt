package io.tujh.imago.presentation.screens.stand

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.domain.ip.Stand
import io.tujh.imago.presentation.components.BlurredBackground
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.TextButton
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.screens.signin.TextFieldShape
import io.tujh.imago.presentation.screens.signin.imagoTFColors
import io.tujh.imago.presentation.theme.colors.ImagoColors

private val localHosts = listOf(Stand.local, Stand.emulatorLocal)

@Composable
fun StandSelectionGreenContent(state: String, onAction: (StandSelectionScreen.Action) -> Unit) {
    BlurredBackground(modifier = Modifier.fillMaxSize()) {
        val navigator = LocalNavigator.currentOrThrow
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.Start),
                iconSource = Icons.AutoMirrored.Filled.KeyboardArrowLeft.asSource(),
                onClick = { navigator.pop() }
            )

            val textFieldColors = imagoTFColors()

            OutlinedTextField(
                value = state,
                onValueChange = { onAction(StandSelectionScreen.Action.Input(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Stand") },
                colors = textFieldColors,
                shape = TextFieldShape,
                singleLine = true,
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                localHosts.fastForEach { ip ->
                    key(ip) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            text = ip.value,
                            onClick = { onAction(StandSelectionScreen.Action.Input(ip.value)) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            onClick = { onAction(StandSelectionScreen.Action.Save(navigator)) },
            shape = CircleShape,
            containerColor = ImagoColors.red,
            contentColor = Color.White
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Filled.Check,
                contentDescription = null
            )
        }
    }
}