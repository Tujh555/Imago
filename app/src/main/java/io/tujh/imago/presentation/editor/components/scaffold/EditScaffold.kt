package io.tujh.imago.presentation.editor.components.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.tujh.imago.presentation.components.Colors
import io.tujh.imago.presentation.components.LocalSharedNavVisibilityScope

private val iconsModifier = Modifier
    .clip(CircleShape)
    .size(32.dp)

@Composable
fun EditScaffold(
    modifier: Modifier = Modifier,
    buttons: List<ScaffoldButton>,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                buttons.fastForEachIndexed { i, button ->
                    key(i) {
                        AnimatedVisibility(
                            visible = button.visible.value,
                            enter = scaleIn() + fadeIn(),
                            exit = scaleOut() + fadeOut()
                        ) {
                            val painter = when (val source = button.source) {
                                is IconSource.Resource -> painterResource(source.id)
                                is IconSource.Vector -> rememberVectorPainter(source.imageVector)
                            }
                            Icon(
                                modifier = iconsModifier.clickable(onClick = button::onClick),
                                contentDescription = null,
                                painter = painter,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        content()
        Spacer(modifier = Modifier.weight(1f))
    }
}