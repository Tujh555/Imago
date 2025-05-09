package io.tujh.imago.presentation.screens.post.comments

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import io.tujh.imago.presentation.components.IconButton
import io.tujh.imago.presentation.components.requestBuilder
import io.tujh.imago.presentation.editor.components.scaffold.asSource
import io.tujh.imago.presentation.locals.LocalFullImageLoader
import io.tujh.imago.presentation.theme.colors.ImagoColors

@Composable
fun PostCommentSheetContent(
    state: PostCommentsScreen.State,
    onAction: (PostCommentsScreen.Action) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
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
        Box(modifier = Modifier
            .fillMaxSize()
            .background(ImagoColors.semitransparent))
        val navigator = LocalNavigator.currentOrThrow

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

            Text(
                text = ":FA:F:DAF ds;f;dslf;ldsf;ldsf;lds;'l sdlf ';dslf ';dsl",
                color = Color.White
            )
        }
    }
}