package io.tujh.imago.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.locals.LocalPreviewImageLoader

@Composable
fun BlurredImage(
    modifier: Modifier = Modifier,
    preview: String,
    full: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.matchParentSize().blur(30.dp),
            model = preview,
            contentDescription = null,
            imageLoader = LocalPreviewImageLoader.current,
            contentScale = ContentScale.FillBounds
        )
        AsyncImage(
            modifier = Modifier.matchParentSize(),
            model = full,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            imageLoader = LocalFullImageLoader.current,
        )
    }
}