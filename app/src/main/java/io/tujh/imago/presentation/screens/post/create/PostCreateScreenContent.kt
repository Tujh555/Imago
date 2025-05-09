package io.tujh.imago.presentation.screens.post.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.hilt.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import io.tujh.imago.R
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.components.applyWith
import io.tujh.imago.presentation.components.end
import io.tujh.imago.presentation.components.rememberReorderHapticFeedback
import io.tujh.imago.presentation.components.requestBuilder
import io.tujh.imago.presentation.components.start
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.locals.LocalUriProvider
import io.tujh.imago.presentation.screens.post.tab.PostTabsModel
import io.tujh.imago.presentation.screens.post.tab.PostTabsScreen
import io.tujh.imago.presentation.theme.colors.ImagoColors
import io.tujh.imago.work.PostUploadWorker
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PostCreateScreenContent(
    state: PostCreateScreen.State,
    onAction: (PostCreateScreen.Action) -> Unit
) {
    val textStyle = TextStyle.Default.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp
    )

    Box(modifier = Modifier.fillMaxSize()) {
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
            Images(state.listState, state.photos, onAction)

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

            AnimatedVisibility(visible = dialogVisible.not()) {
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

            AddImageButton(state.canPickCount, onAction)
        }

        val navigator = LocalNavigator.currentOrThrow

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            visible = state.createVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val context = LocalContext.current
            val model = LocalNavigator.currentOrThrow.getNavigatorScreenModel<PostTabsModel>()

            FloatingActionButton(
                onClick = {
                    val operation = state.run {
                        PostUploadWorker.start(context, title, photos.map(Uri::parse))
                    }
                    model.onAction(PostTabsScreen.Action.OnAdded(operation))
                    navigator.pop()
                },
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

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 24.dp, top = 8.dp),
            iconSource = Icons.AutoMirrored.Filled.KeyboardArrowLeft.asSource(),
            onClick = { navigator.pop() }
        )

        InputDialog(
            dialogVisible = dialogVisible,
            title = state.title,
            textStyle = textStyle,
            titleChanged = { onAction(PostCreateScreen.Action.Title(it)) },
            dismiss = { dialogVisible = false }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Images(
    listState: LazyListState,
    images: List<String>,
    onAction: (PostCreateScreen.Action) -> Unit
) {
    val haptic = rememberReorderHapticFeedback()
    val reorderableGridState = rememberReorderableLazyListState(listState) { from, to ->
        onAction(PostCreateScreen.Action.Reorder(to.index, from.index))
    }
    val configuration = LocalConfiguration.current
    val (height, width) = remember(configuration.screenHeightDp, configuration.screenWidthDp) {
        (configuration.screenHeightDp * 0.4f).dp to (configuration.screenWidthDp * 0.8f).dp
    }

    Box {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            state = listState,
        ) {
            itemsIndexed(images, key = { _, uri -> uri }) { index, uri ->
                val interactionSource = remember { MutableInteractionSource() }
                ReorderableItem(reorderableGridState, uri) { isDragging ->
                    val scale by animateFloatAsState(if (isDragging) 0.9f else 1f)
                    Box(
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    ) {
                        val navigator = LocalNavigator.currentOrThrow
                        val sharedKey = "editing-image-$index"
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(max = width)
                                .applyWith(LocalSharedTransitionScope.current) {
                                    it.sharedElement(
                                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize,
                                        state = rememberSharedContentState(
                                            key = sharedKey
                                        ),
                                        animatedVisibilityScope = LocalSharedNavVisibilityScope.current
                                    )
                                }
                                .semantics {
                                    customActions = listOf(
                                        CustomAccessibilityAction(
                                            label = "Move Up",
                                            action = {
                                                val handle = index > 0
                                                if (handle) {
                                                    val action = PostCreateScreen.Action.Reorder(
                                                        to = index - 1,
                                                        from = index
                                                    )
                                                    onAction(action)
                                                }
                                                handle
                                            }
                                        ),
                                        CustomAccessibilityAction(
                                            label = "Move Down",
                                            action = {
                                                val handle = index < images.size - 1
                                                if (handle) {
                                                    val action = PostCreateScreen.Action.Reorder(
                                                        to = index + 1,
                                                        from = index
                                                    )
                                                    onAction(action)
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
                                .clearAndSetSemantics { }
                                .clickable {
                                    onAction(
                                        PostCreateScreen.Action.Edit(sharedKey, uri, navigator)
                                    )
                                },
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            model = uri.requestBuilder().build(),
                            imageLoader = LocalFullImageLoader.current
                        )

                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.inverseOnSurface)
                                .padding(4.dp)
                                .align(Alignment.TopEnd)
                                .clickable { onAction(PostCreateScreen.Action.Remove(uri)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .height(height)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .applyWith(LocalSharedNavVisibilityScope.current) {
                        it.animateEnterExit(fadeIn(), fadeOut())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add at least one image",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.AddImageButton(
    canPickCount: Int,
    onAction: (PostCreateScreen.Action) -> Unit
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    var uriForCamera by remember { mutableStateOf<Uri?>(null) }

    val multipleGalleryLauncher = canPickCount.takeIf { it > 1 }?.let { count ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(count),
            onResult = { uris -> onAction(PostCreateScreen.Action.Picked(uris)) }
        )
    }
    val singleGalleryLauncher = canPickCount.takeIf(1::equals)?.let {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> uri?.let { onAction(PostCreateScreen.Action.Picked(it)) } }
        )
    }
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { saved ->
            if (saved) {
                uriForCamera?.let { onAction(PostCreateScreen.Action.Picked(it)) }
            }
            uriForCamera = null
        }
    )
    val context = LocalContext.current
    var cameraPermitted by remember {
        val checkRes = context.checkSelfPermission(Manifest.permission.CAMERA)
        mutableStateOf(checkRes == PackageManager.PERMISSION_GRANTED)
    }
    val getPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            cameraPermitted = permissions[Manifest.permission.CAMERA] == true
        }
    )

    AnimatedVisibility(visible = canPickCount > 0) {
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
                                        tooltipState.dismiss()
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
                                        if (cameraPermitted) {
                                            scope.launch {
                                                val uri = uriProvider.temporaryImage()
                                                uriForCamera = uri
                                                cameraLauncher.launch(uri)
                                                tooltipState.dismiss()
                                            }
                                        } else {
                                            getPermissions.launch(
                                                arrayOf(Manifest.permission.CAMERA)
                                            )
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun InputDialog(
    dialogVisible: Boolean,
    title: String,
    textStyle: TextStyle,
    titleChanged: (String) -> Unit,
    dismiss: () -> Unit
) {
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
                            onClick = dismiss
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
                        var text by remember { mutableStateOf(title) }

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
                                    titleChanged(text)
                                    dismiss()
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