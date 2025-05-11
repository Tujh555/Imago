package io.tujh.imago.presentation.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.R
import io.tujh.imago.presentation.components.BlurredBackground
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.UserAvatar
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.screens.post.create.AddImageTooltip
import io.tujh.imago.presentation.screens.post.create.PostCreateScreen
import io.tujh.imago.presentation.screens.post.list.PostListScreen
import io.tujh.imago.presentation.screens.signin.TextFieldShape
import io.tujh.imago.presentation.screens.signin.imagoTFColors
import io.tujh.imago.presentation.screens.signup.SignUpScreen
import io.tujh.imago.presentation.theme.colors.ImagoColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(state: ProfileScreen.State, onAction: (ProfileScreen.Action) -> Unit) {
    BlurredBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            AddImageTooltip(
                canPickCount = 1,
                onPicked = { uris ->
                    uris.firstOrNull()?.let {
                        onAction(ProfileScreen.Action.Picked(it, navigator))
                    }
                }
            ) { scope, tooltipState ->
                UserAvatar(
                    modifier = Modifier.size(128.dp),
                    url = state.avatar,
                    userId = state.id,
                    onClick = { scope.launch { tooltipState.show() } }
                )
            }

            val textFieldColors = imagoTFColors()

            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(ProfileScreen.Action.Name(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Name") },
                colors = textFieldColors,
                shape = TextFieldShape,
                singleLine = true,
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            visible = state.isLoading,
            enter = slideInVertically { -it },
            exit = slideOutVertically { -it }
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .clip(CircleShape)
                    .background(PullToRefreshDefaults.containerColor, CircleShape)
                    .padding(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    trackColor = PullToRefreshDefaults.containerColor,
                    color = PullToRefreshDefaults.indicatorColor
                )
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            visible = state.finishVisible && state.isLoading.not(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            FloatingActionButton(
                onClick = { onAction(ProfileScreen.Action.Save) },
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
}