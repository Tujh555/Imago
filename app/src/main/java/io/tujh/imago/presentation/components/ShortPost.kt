package io.tujh.imago.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import coil3.compose.AsyncImage
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.locals.LocalPreviewImageLoader
import io.tujh.imago.presentation.models.ShortPostItem
import io.tujh.imago.presentation.theme.colors.ImagoColors

private val textBackgroundBrush = Brush.verticalGradient(
    0f to ImagoColors.semitransparent.copy(alpha = 0f),
    1f to ImagoColors.semitransparent.copy(alpha = 0.2f)
)
val postShape = RoundedCornerShape(8.dp)

@Composable
fun ShortPost(
    modifier: Modifier = Modifier,
    post: ShortPostItem,
    shimmer: Shimmer
) {
    BoxWithConstraints(modifier = modifier.clip(postShape)) {
        val density = LocalDensity.current
        val original = post.originalSize
        val imageSize = remember(original, maxWidth, maxHeight) {
            val scale = ContentScale.Fit
            val boxSize = with(density) { DpSize(maxWidth, maxHeight).toSize() }
            val scaleFactor = scale.computeScaleFactor(original.toSize(), boxSize)

            with(density) { (original * scaleFactor).toDpSize() }
        }
        Box(
            modifier = Modifier.size(imageSize),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.outline)
                    .matchParentSize()
                    .shimmer(shimmer)
            )
            AsyncImage(
                modifier = Modifier.matchParentSize(),
                model = post.urls.second,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                imageLoader = LocalFullImageLoader.current,
            )
        }

//        BlurredImage(
//            modifier = Modifier.size(imageSize),
//            preview = post.urls.first,
//            full = post.urls.second
//        )

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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    text = post.title,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }
    }
}

private operator fun IntSize.times(factor: ScaleFactor): Size =
    Size(width * factor.scaleX, height * factor.scaleY)