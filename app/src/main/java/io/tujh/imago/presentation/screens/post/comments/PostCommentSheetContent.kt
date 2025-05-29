package io.tujh.imago.presentation.screens.post.comments

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import io.tujh.imago.R
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.UserAvatar
import io.tujh.imago.presentation.components.requestBuilder
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.models.CommentItem
import io.tujh.imago.presentation.screens.post.list.isLoading
import io.tujh.imago.presentation.theme.colors.ImagoColors

private val keyPath = arrayOf("**")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCommentSheetContent(
    state: PostCommentsScreen.State,
    onAction: (PostCommentsScreen.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .blur(100.dp),
            model = state.currentUrl.requestBuilder().build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            imageLoader = LocalFullImageLoader.current,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ImagoColors.semitransparent)
        )
        val navigator = LocalNavigator.currentOrThrow
        var bottomPadding by remember { mutableStateOf(0.dp) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .systemBarsPadding()
        ) {
            IconButton(
                iconSource = Icons.AutoMirrored.Filled.KeyboardArrowLeft.asSource(),
                padding = 10.dp,
                onClick = { navigator.pop() }
            )

            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(PostCommentsScreen.Action.Refresh) },
            ) {
                val isEmpty = state.run {
                    loadState == LoadState.Loaded && state.comments.isEmpty()
                }

                if (isEmpty) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "There are no comments here yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                } else {
                    val shimmer = rememberShimmer(ShimmerBounds.View)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        state = state.lazyListState,
                        contentPadding = PaddingValues(top = 16.dp, bottom = bottomPadding + 16.dp)
                    ) {
                        items(
                            state.comments,
                            key = { it.id },
                            contentType = { "comment" }
                        ) { comment ->
                            Comment(
                                modifier = Modifier.fillMaxWidth(),
                                comment = comment
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (state.loadState.isLoading()) {
                            items(30, contentType = { "shimmer" }) {
                                CommentShimmer(
                                    modifier = Modifier.fillMaxWidth(),
                                    shimmer = shimmer
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }

        val textStyle = TextStyle.Default.copy(
            color = Color.White,
            fontSize = 12.sp
        )
        val density = LocalDensity.current
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .navigationBarsPadding()
                .onSizeChanged {
                    bottomPadding = with(density) {
                        it.height.toDp()
                    }
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier.weight(1f),
                value = state.commentText,
                onValueChange = { onAction(PostCommentsScreen.Action.Comment(it)) },
                maxLines = 10,
                textStyle = textStyle
            ) { innerTextField ->
                innerTextField()
                if (state.commentText.isEmpty()) {
                    Text(text = "Comment", style = textStyle)
                }
            }

            val sendActive = state.run {
                commentText.isNotBlank() && isRefreshing.not()
            }
            val tint by animateColorAsState(
                targetValue = if (sendActive.not()) {
                    Color.White.copy(alpha = 0.5f)
                } else {
                    Color.White
                }
            )

            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(enabled = sendActive) { onAction(PostCommentsScreen.Action.SendComment) },
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                tint = tint
            )
        }
    }
}

private val shimmerShape = RoundedCornerShape(8.dp)

@Composable
private fun CommentShimmer(
    modifier: Modifier = Modifier,
    shimmer: Shimmer
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .shimmer(shimmer),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline)
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(15.dp)
                    .clip(shimmerShape)
                    .background(MaterialTheme.colorScheme.outline)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clip(shimmerShape)
                    .background(MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
private fun Comment(
    modifier: Modifier = Modifier,
    comment: CommentItem,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatar(
            modifier = Modifier.size(32.dp),
            url = null,
            userId = comment.author.id
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = comment.author.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = comment.text,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.White,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = comment.createdAt,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Color(0xFFADADB8),
                )

                AnimatedContent(
                    targetState = comment.status,
                    transitionSpec = {
                        expandHorizontally() + fadeIn() togetherWith shrinkHorizontally() + fadeOut()
                    }
                ) { status ->
                    when (status) {
                        Comment.Status.Sending -> {
                            val composition =
                                rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_clock))
                            val progress = animateLottieCompositionAsState(
                                composition.value,
                                iterations = Int.MAX_VALUE
                            )
                            val dynamicProperties = rememberLottieDynamicProperties(
                                rememberLottieDynamicProperty(
                                    property = LottieProperty.COLOR_FILTER,
                                    value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                                        Color.White.hashCode(),
                                        BlendModeCompat.SRC_ATOP
                                    ),
                                    keyPath = keyPath
                                )
                            )
                            LottieAnimation(
                                modifier = Modifier.size(16.dp),
                                composition = composition.value,
                                progress = { progress.value },
                                dynamicProperties = dynamicProperties
                            )
                        }

                        Comment.Status.Error -> Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(R.drawable.ic_error),
                            contentDescription = null,
                            tint = Color(0xFFA63232)
                        )

                        Comment.Status.Empty -> Unit
                    }
                }
            }
        }
    }
}