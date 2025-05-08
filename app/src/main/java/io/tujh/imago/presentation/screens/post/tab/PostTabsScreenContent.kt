package io.tujh.imago.presentation.screens.post.tab

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.R
import io.tujh.imago.presentation.components.BlurredBackground
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.screens.post.create.PostCreateScreen
import io.tujh.imago.presentation.screens.profile.ProfileScreen
import io.tujh.imago.presentation.theme.colors.ImagoColors
import kotlinx.coroutines.launch

@Composable
fun PostTabsScreenContent(state: PostTabsScreen.State, onAction: (PostTabsScreen.Action) -> Unit) {
    BlurredBackground(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        var notificationsPermitted by remember {
            val checkRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                PackageManager.PERMISSION_GRANTED
            }
            mutableStateOf(checkRes == PackageManager.PERMISSION_GRANTED)
        }
        val getNotificationPermission = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationsPermitted =
                        permissions[Manifest.permission.POST_NOTIFICATIONS] == true
                }
            }
        )

        LaunchedEffect(Unit) {
            if (notificationsPermitted.not() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getNotificationPermission.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            IconButton(
                modifier = Modifier.padding(start = 24.dp),
                iconSource = R.drawable.ic_profile.asSource(),
                padding = 10.dp
            ) {
                navigator.push(ProfileScreen())
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                state.tabs.fastForEachIndexed { index, component ->
                    key(index) {
                        val selected = index == state.pagerState.currentPage
                        val textColor = if (selected) {
                            Color.Black
                        } else {
                            Color.White
                        }
                        val backgroundColor = if (selected) {
                            Color.White
                        } else {
                            Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    scope.launch { state.pagerState.animateScrollToPage(index) }
                                }
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = component.title,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        if (index != state.tabs.lastIndex) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = state.pagerState,
                key = { state.tabs[it].key },
            ) { page ->
                state.tabs[page].Content()
            }
        }

        FloatingActionButton(
            onClick = { navigator.push(PostCreateScreen()) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 24.dp),
            shape = CircleShape,
            containerColor = ImagoColors.red,
            contentColor = Color.White
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
        }
    }
}