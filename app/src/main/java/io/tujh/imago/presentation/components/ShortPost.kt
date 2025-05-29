package io.tujh.imago.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.valentinilk.shimmer.Shimmer
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.models.PostImageItem
import io.tujh.imago.presentation.models.PostItem
import io.tujh.imago.presentation.screens.post.list.PostShimmer
import io.tujh.imago.presentation.theme.colors.ImagoColors

private val textBackgroundBrush = Brush.verticalGradient(
    0f to ImagoColors.semitransparent.copy(alpha = 0f),
    1f to ImagoColors.semitransparent.copy(alpha = 0.2f)
)
val postShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ShortPost(
    modifier: Modifier = Modifier,
    post: PostItem,
    shimmer: Shimmer,
    imageSharedKey: String,
    titleSharedKey: String,
    onClick: () -> Unit
) {
    BoxWithConstraints(modifier = modifier.clip(postShape).clickable(onClick = onClick)) {
        val image = post.images.first()
        val imageSize = imageContainerSize(image)

        Box(
            modifier = Modifier.size(imageSize),
            contentAlignment = Alignment.Center
        ) {
            PostShimmer(shimmer = shimmer, height = imageSize.height)
            val url = image.url

            with(LocalSharedTransitionScope.current) {
                AsyncImage(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(postShape)
                        .sharedElement(
                            state = rememberSharedContentState(key = imageSharedKey),
                            animatedVisibilityScope = LocalSharedNavVisibilityScope.current,
                            clipInOverlayDuringTransition = OverlayClip(postShape),
                        ),
                    model = url.requestBuilder().build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    imageLoader = LocalFullImageLoader.current,
                )
            }
        }

        if (post.title.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(textBackgroundBrush)
                        .blur(30.dp)
                )
                Text(
                    modifier = Modifier
                        .applyWith(LocalSharedTransitionScope.current) {
                            it.sharedElement(
                                state = rememberSharedContentState(titleSharedKey),
                                animatedVisibilityScope = LocalSharedNavVisibilityScope.current,
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = post.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun BoxWithConstraintsScope.imageContainerSize(image: PostImageItem): DpSize = with(LocalDensity.current) {
    val scale = ContentScale.Fit
    val boxSize = DpSize(maxWidth, maxHeight).toSize()
    val originalSize = Size(image.width.toFloat(), image.height.toFloat())
    val scaleFactor = scale.computeScaleFactor(originalSize, boxSize)

    (originalSize * scaleFactor).toDpSize()
}

@Composable
@ReadOnlyComposable
fun String.requestBuilder(): ImageRequest.Builder {
    val context = LocalContext.current
    return ImageRequest
        .Builder(context)
        .data(this)
        .memoryCacheKey(this)
}

private operator fun Size.times(factor: ScaleFactor): Size =
    Size(width * factor.scaleX, height * factor.scaleY)