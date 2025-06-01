package io.tujh.imago.presentation.screens.post.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import io.tujh.imago.R
import io.tujh.imago.presentation.components.Button
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope
import io.tujh.imago.presentation.components.LocalSharedTransitionScope
import io.tujh.imago.presentation.components.applyIf
import io.tujh.imago.presentation.components.imageContainerSize
import io.tujh.imago.presentation.components.postShape
import io.tujh.imago.presentation.components.requestBuilder
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.screens.post.comments.PostCommentsScreen
import io.tujh.imago.presentation.theme.colors.ImagoColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PostViewScreenContent(state: PostViewScreen.State, onAction: (PostViewScreen.Action) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val settledPage = state.pagerState.currentPage
        val backgroundUrl = state.post.images[settledPage].url
        AnimatedContent(
            targetState = backgroundUrl,
            transitionSpec = { fadeIn(initialAlpha = 0.5f) togetherWith fadeOut(targetAlpha = 0.8f) }
        ) { url ->
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(100.dp),
                model = url.requestBuilder().build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                imageLoader = LocalFullImageLoader.current,
            )
        }

        val images = state.post.images
        val pagerState = state.pagerState
        with(LocalSharedTransitionScope.current) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .systemBarsPadding()
            ) {
                val navigator = LocalNavigator.currentOrThrow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        iconSource = Icons.AutoMirrored.Filled.KeyboardArrowLeft.asSource(),
                        padding = 10.dp,
                        onClick = { navigator.pop() }
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .skipToLookaheadSize()
                                .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                                .sharedElement(
                                    state = rememberSharedContentState(state.titleSharedKey),
                                    animatedVisibilityScope = LocalSharedNavVisibilityScope.current
                                ),
                            text = state.post.title,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Button(
                        padding = 10.dp,
                        onClick = { onAction(PostViewScreen.Action.MarkFavorite) },
                        content = {
                            AnimatedContent(
                                targetState = state.inFavorite,
                                transitionSpec = {
                                    val enter = fadeIn(initialAlpha = 0.5f) + scaleIn(initialScale = 0.5f)
                                    val exit = fadeOut(targetAlpha = 0.5f) + scaleOut(targetScale = 0.5f)
                                    enter togetherWith exit
                                }
                            ) { inFavorite ->
                                val vector = if (inFavorite) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                }

                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = vector,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxSize(),
                        state = pagerState,
                        verticalAlignment = Alignment.CenterVertically,
                        pageSpacing = 16.dp,
                        key = { it },
                    ) { page ->
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val image = images[page]
                            val url = image.url
                            val imageSize = imageContainerSize(image)

                            AsyncImage(
                                modifier = Modifier
                                    .size(imageSize)
                                    .clip(postShape)
                                    .graphicsLayer {
                                        val startOffset = pagerState.startOffsetForPage(page)
                                        val blurRadius = (startOffset * 25f).coerceAtLeast(0.1f)
                                        renderEffect = BlurEffect(
                                            radiusX = blurRadius,
                                            radiusY = blurRadius,
                                            edgeTreatment = TileMode.Decal
                                        )
                                        alpha = (2f - startOffset) / 2f
                                    }
                                    .applyIf(page == 0) {
                                        sharedElement(
                                            state = rememberSharedContentState(state.imageSharedKey),
                                            animatedVisibilityScope = LocalSharedNavVisibilityScope.current,
                                            clipInOverlayDuringTransition = OverlayClip(postShape)
                                        )
                                    },
                                model = url.requestBuilder().build(),
                                contentDescription = null,
                                imageLoader = LocalFullImageLoader.current,
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }

                    IconButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 24.dp, end = 8.dp),
                        iconSource = R.drawable.ic_comments.asSource(),
                        onClick = {
                            val screen = PostCommentsScreen(state.post, backgroundUrl)
                            navigator.push(screen)
                        }
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .clip(CircleShape)
                            .background(ImagoColors.semitransparent)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        SlideCounter(pagerState)
                    }
                }
            }
        }
    }
}

@Composable
private fun SlideCounter(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AnimatedContent(
            targetState = pagerState.currentPage,
            transitionSpec = {
                val spec = if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                }
                spec using SizeTransform(clip = false)
            }
        ) { current ->
            Text(
                text = "${current + 1}",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Text(
            text = " of ${pagerState.pageCount}",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

fun PagerState.offsetForPage(page: Int) = currentPage - page + currentPageOffsetFraction

fun PagerState.startOffsetForPage(page: Int) = offsetForPage(page).coerceAtLeast(0f)