package io.tujh.imago.presentation.screens.signin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.presentation.components.BlurredBackground
import io.tujh.imago.presentation.components.PasswordTextField
import io.tujh.imago.presentation.components.TextButton
import io.tujh.imago.presentation.components.screenPadding
import io.tujh.imago.presentation.screens.signup.SignUpScreen
import io.tujh.imago.presentation.screens.stand.StandSelectionScreen

@Composable
fun imagoTFColors() = OutlinedTextFieldDefaults.colors().copy(
    focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,

)

val TextFieldShape = RoundedCornerShape(8.dp)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignInScreenContent(state: SignInScreen.State, onAction: (SignInScreen.Action) -> Unit) {
    BlurredBackground {
        val navigator = LocalNavigator.currentOrThrow
        Column(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = {},
                    onLongClick = { navigator.push(StandSelectionScreen()) }
                )
                .systemBarsPadding()
                .screenPadding()
                .imePadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Sign In",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            val textFieldColors = imagoTFColors()
            OutlinedTextField(
                value = state.email,
                onValueChange = { onAction(SignInScreen.Action.Email(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                label = { Text(text = "Email") },
                colors = textFieldColors,
                shape = TextFieldShape,
                singleLine = true,
            )

            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                text = state.password,
                colors = textFieldColors,
                shape = TextFieldShape,
                onValueChange = { onAction(SignInScreen.Action.Password(it)) },
            )

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                shape = TextFieldShape,
                text = "Log in",
                active = state.isLoading.not(),
                onClick = { onAction(SignInScreen.Action.Login(navigator)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "or",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White, CircleShape)
                    .clickable { navigator.push(SignUpScreen()) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Create account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}