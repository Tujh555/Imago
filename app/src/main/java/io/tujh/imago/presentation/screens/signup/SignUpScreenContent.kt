package io.tujh.imago.presentation.screens.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.presentation.components.BlurredBackground
import io.tujh.imago.presentation.components.Button
import io.tujh.imago.presentation.components.PasswordTextField
import io.tujh.imago.presentation.components.TextButton
import io.tujh.imago.presentation.components.screenPadding
import io.tujh.imago.presentation.screens.signin.TextFieldShape
import io.tujh.imago.presentation.screens.signin.imagoTFColors

@Composable
fun SignUpScreenContent(state: SignUpScreen.State, onAction: (SignUpScreen.Action) -> Unit) {
    BlurredBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .screenPadding()
                .imePadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    padding = 10.dp,
                    onClick = { navigator.pop() }
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Sign Up",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            val textFieldColors = imagoTFColors()

            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(SignUpScreen.Action.Name(it)) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                label = { Text(text = "Name") },
                colors = textFieldColors,
                shape = TextFieldShape,
                singleLine = true,
                isError = state.nameCorrect.not()
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { onAction(SignUpScreen.Action.Email(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Email") },
                colors = textFieldColors,
                shape = TextFieldShape,
                singleLine = true,
                isError = state.emailCorrect.not()
            )

            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                text = state.password,
                colors = textFieldColors,
                shape = TextFieldShape,
                onValueChange = { onAction(SignUpScreen.Action.Password(it)) },
                error = state.passwordCorrect.not()
            )

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                shape = TextFieldShape,
                text = "Log in",
                active = state.isLoading.not(),
                onClick = { onAction(SignUpScreen.Action.SignUp(navigator)) }
            )
        }
    }
}