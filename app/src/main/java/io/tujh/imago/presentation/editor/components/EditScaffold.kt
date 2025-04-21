package io.tujh.imago.presentation.editor.components

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.tujh.imago.R
import io.tujh.imago.presentation.components.Colors
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope

@Composable
fun EditScaffold(
    modifier: Modifier = Modifier,
    undo: () -> Unit,
    save: () -> Unit,
    close: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.systemBarsPadding()) {
        with(LocalSharedNavVisibilityScope.current) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colors.semitransparent)
                    .padding(8.dp)
                    .animateEnterExit(
                        enter = fadeIn() + slideInVertically { -it },
                        exit = fadeOut() + slideOutVertically { -it },
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape).size(32.dp).clickable(onClick = close),
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color.White
                )

                Icon(
                    modifier = Modifier.clip(CircleShape).size(32.dp).clickable(onClick = undo),
                    painter = painterResource(R.drawable.ic_arrow_revert),
                    contentDescription = null,
                    tint = Color.White
                )

                Icon(
                    modifier = Modifier.clip(CircleShape).size(32.dp).clickable(onClick = save),
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        content()
    }
}