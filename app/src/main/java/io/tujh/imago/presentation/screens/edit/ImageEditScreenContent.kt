package io.tujh.imago.presentation.screens.edit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import io.tujh.imago.presentation.components.Colors
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope

@Composable
fun ImageEditScreenContent(
    state: ImageEditScreen.State,
    onAction: (ImageEditScreen.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = state.loadingState,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { loadingState ->
            when (loadingState) {
                LoadingState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    content = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(56.dp),
                            color = Color.White
                        )
                    }
                )

                LoadingState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            text = "Невозможно загрузить фотографию",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )

                LoadingState.Success -> SuccessBody(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SuccessBody(
    modifier: Modifier,
    state: ImageEditScreen.State,
    onAction: (ImageEditScreen.Action) -> Unit
) {
    with(LocalSharedTransitionScope.current) {
        AnimatedContent(modifier = modifier, targetState = state.editingComponent) { editing ->
            CompositionLocalProvider(LocalSharedNavVisibilityScope provides this) {
                if (editing != null) {
                    editing.Content()
                } else {
                    Column(Modifier.fillMaxSize()) {
                        state.image?.let { image ->
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .sharedElement(
                                        state = rememberSharedContentState(
                                            key = "editing-image"
                                        ),
                                        animatedVisibilityScope = this@AnimatedContent
                                    ),
                                contentScale = ContentScale.Crop,
                                bitmap = image,
                                contentDescription = null
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Colors.semitransparent)
                                .horizontalScroll(rememberScrollState())
                                .padding(8.dp)
                                .animateEnterExit(
                                    enter = fadeIn() + slideInVertically { it },
                                    exit = fadeOut() + slideOutVertically { it },
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditFactory.entries.fastForEachIndexed { i, component ->
                                key(i) {
                                    IconButton(
                                        modifier = Modifier.size(48.dp),
                                        iconRes = component.icon,
                                        iconTint = Color.White,
                                        onClick = {
                                            onAction(
                                                ImageEditScreen.Action.SelectComponent(
                                                    component
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}