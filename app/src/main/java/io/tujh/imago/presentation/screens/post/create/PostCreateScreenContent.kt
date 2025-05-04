package io.tujh.imago.presentation.screens.post.create

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import io.tujh.imago.R
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.components.applyNotNull
import io.tujh.imago.presentation.components.end
import io.tujh.imago.presentation.components.move
import io.tujh.imago.presentation.components.rememberReorderHapticFeedback
import io.tujh.imago.presentation.components.requestBuilder
import io.tujh.imago.presentation.components.start
import io.tujh.imago.presentation.locals.LocalUriProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private val shapeForSharedElement = RoundedCornerShape(16.dp)

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun PostCreateScreenContent(
    state: PostCreateScreen.State,
    onAction: (PostCreateScreen.Action) -> Unit
) {
    val textStyle = TextStyle.Default.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp
    )
    var uriForCamera by remember { mutableStateOf<Uri?>(null) }

    val multipleGalleryLauncher = state.canPickCount.takeIf { it > 1 }?.let { count ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(count),
            onResult = { uris -> onAction(PostCreateScreen.Action.Picked(uris)) }
        )
    }
    val singleGalleryLauncher = state.canPickCount.takeIf(1::equals)?.let {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> uri?.let { onAction(PostCreateScreen.Action.Picked(it)) } }
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { saved ->
            if (saved) {
                uriForCamera?.let { onAction(PostCreateScreen.Action.Picked(it)) }
            }
        }
    )
    var cameraPermitted by remember {
        mutableStateOf()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        var dialogVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            val haptic = rememberReorderHapticFeedback()
            val reorderableGridState =
                rememberReorderableLazyListState(state.listState) { from, to ->
                    onAction(PostCreateScreen.Action.Reorder(to.index, from.index))
                    haptic.move()
                }
            val configuration = LocalConfiguration.current
            val (height, width) = remember(configuration.screenHeightDp, configuration.screenWidthDp) {
                (configuration.screenHeightDp * 0.4f).dp to (configuration.screenWidthDp / 2f).dp
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                state = state.listState,
            ) {
                itemsIndexed(state.photos, key = { _, uri -> uri }) { index, uri ->
                    val interactionSource = remember { MutableInteractionSource() }
                    ReorderableItem(reorderableGridState, uri) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(max = width)
                                .semantics {
                                    customActions = listOf(
                                        CustomAccessibilityAction(
                                            label = "Move Up",
                                            action = {
                                                if (index > 0) {
                                                    onAction(
                                                        PostCreateScreen.Action.Reorder(
                                                            to = index - 1,
                                                            from = index
                                                        )
                                                    )
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        ),
                                        CustomAccessibilityAction(
                                            label = "Move Down",
                                            action = {
                                                if (index < state.photos.size - 1) {
                                                    onAction(
                                                        PostCreateScreen.Action.Reorder(
                                                            to = index + 1,
                                                            from = index
                                                        )
                                                    )
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        ),
                                    )
                                }
                                .longPressDraggableHandle(
                                    onDragStarted = { haptic.start() },
                                    onDragStopped = { haptic.end() },
                                    interactionSource = interactionSource,
                                )
                                .clearAndSetSemantics { },
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            model = uri.requestBuilder().build()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceBright)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val text = state.title.ifEmpty { "Write something..." }

            androidx.compose.animation.AnimatedVisibility(visible = dialogVisible.not()) {
                Text(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { dialogVisible = true }
                        )
                        .padding(vertical = 16.dp)
                        .let { modifier ->
                            with(LocalSharedTransitionScope.current) {
                                modifier
                                    .sharedElement(
                                        state = rememberSharedContentState(key = "title"),
                                        animatedVisibilityScope = this@AnimatedVisibility,
                                    )
                                    .skipToLookaheadSize()
                            }
                        },
                    text = text,
                    style = textStyle
                )
            }

            val tooltipState = rememberTooltipState(isPersistent = true)
            val scope = rememberCoroutineScope()
            androidx.compose.animation.AnimatedVisibility(
                visible = state.canPickCount > 0
            ) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    state = tooltipState,
                    tooltip = {
                        RichTooltip(
                            title = null,
                            caretSize = TooltipDefaults.caretSize,
                            text = {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                val request = PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                                when {
                                                    multipleGalleryLauncher != null -> {
                                                        multipleGalleryLauncher.launch(request)
                                                    }
                                                    singleGalleryLauncher != null -> {
                                                        singleGalleryLauncher.launch(request)
                                                    }
                                                }
                                            },
                                        contentDescription = null,
                                        painter = painterResource(R.drawable.ic_galerry),
                                        tint = Color.White
                                    )
                                    val uriProvider = LocalUriProvider.current
                                    Icon(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                when (cameraPermissionState.status) {
                                                    is PermissionStatus.Denied ->
                                                        cameraPermissionState.launchPermissionRequest()
                                                    PermissionStatus.Granted -> {
                                                        scope.launch {
                                                            uriProvider?.temporaryImage()?.let { uri ->
                                                                cameraLauncher.launch(uri)
                                                                uriForCamera = uri
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        contentDescription = null,
                                        painter = painterResource(R.drawable.ic_camera),
                                        tint = Color.White
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .clickable { scope.launch { tooltipState.show() } }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            contentDescription = null,
                            imageVector = Icons.Filled.Add,
                            tint = Color.White
                        )

                        Text(text = "Photo")
                    }
                }
            }
        }

        AnimatedContent(
            targetState = dialogVisible,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
        ) { visible ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                if (visible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { dialogVisible = false }
                            )
                            .background(Color.Black.copy(alpha = 0.5f))
                    )

                    with(LocalSharedTransitionScope.current) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            var text by remember { mutableStateOf(state.title) }

                            BasicTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .sharedElement(
                                        state = rememberSharedContentState(key = "title"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                    )
                                    .skipToLookaheadSize(),
                                value = text,
                                onValueChange = { text = it },
                                textStyle = textStyle,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        onAction(PostCreateScreen.Action.Title(text))
                                        dialogVisible = false
                                    }
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                                decorationBox = { innerTextField ->
                                    innerTextField()
                                    if (text.isEmpty()) {
                                        Text(
                                            text = "Write something...",
                                            style = textStyle,
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}