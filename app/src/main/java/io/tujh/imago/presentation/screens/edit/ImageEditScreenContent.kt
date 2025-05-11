package io.tujh.imago.presentation.screens.edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.R
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.components.applyWith
import io.tujh.imago.presentation.editor.components.filters.FiltersComponent
import io.tujh.imago.presentation.editor.components.filters.shader.ShaderFilter
import io.tujh.imago.presentation.editor.components.filters.shader.rememberShaderFilters
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.theme.colors.ImagoColors
import kotlinx.coroutines.launch

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
        SuccessBody(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SuccessBody(
    modifier: Modifier,
    state: ImageEditScreen.State,
    onAction: (ImageEditScreen.Action) -> Unit
) {
    val sharedScope = LocalSharedTransitionScope.current
    val navVisibilityScope = LocalSharedNavVisibilityScope.current
    val navigator = LocalNavigator.currentOrThrow

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { navigator.pop() }
                    .padding(4.dp),
                text = "Cancel",
                fontSize = 16.sp,
            )

            Text(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onAction(ImageEditScreen.Action.Save) }
                    .padding(4.dp),
                text = "Save",
                fontSize = 16.sp,
            )
        }
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .applyWith(sharedScope) {
                    it.sharedElement(
                        state = rememberSharedContentState(
                            key = state.sharedKey
                        ),
                        animatedVisibilityScope = navVisibilityScope
                    )
                },
            contentScale = ContentScale.Fit,
            bitmap = state.image,
            contentDescription = null
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ImagoColors.semitransparent)
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
                .applyWith(navVisibilityScope) {
                    it.animateEnterExit(
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it },
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            EditFactory.entries.fastForEachIndexed { i, factory ->
                key(i) {
                    IconButton(
                        modifier = Modifier.size(48.dp),
                        iconSource = factory.icon.asSource(),
                        tint = Color.White,
                        onClick = {
                            val component = factory(
                                bitmap = state.image,
                                sharedKey = state.sharedKey,
                                saver = {
                                    onAction(ImageEditScreen.Action.Update(it))
                                    navigator.pop()
                                }
                            )
                            navigator.push(component)
                        }
                    )
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val tooltipState = rememberTooltipState(isPersistent = true)
                val scope = rememberCoroutineScope()
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        RichTooltip(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 530.dp)
                                .padding(horizontal = 16.dp),
                            title = { Text("Choose filter") },
                            caretSize = TooltipDefaults.caretSize
                        ) {
                            Filters(image = state.image) {
                                tooltipState.dismiss()
                                val component = FiltersComponent(state.sharedKey, it, state.image) {
                                    onAction(ImageEditScreen.Action.Update(it))
                                    navigator.pop()
                                }
                                navigator.push(component)
                            }
                        }
                    },
                    state = tooltipState
                ) {
                    IconButton(
                        modifier = Modifier.size(48.dp),
                        iconSource = R.drawable.ic_filter.asSource(),
                        tint = Color.White,
                        onClick = {
                            scope.launch {
                                tooltipState.show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun Filters(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    onClick: (ShaderFilter) -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val shape = RoundedCornerShape(4.dp)
        rememberShaderFilters().fastForEach { shader ->
            key(shader.name) {
                Image(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(shape)
                        .onSizeChanged { shader.onSizeChanged(it) }
                        .graphicsLayer { shader.apply() }
                        .clickable { onClick(shader) },
                    bitmap = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}