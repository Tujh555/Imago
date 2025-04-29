package io.tujh.imago.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import io.tujh.imago.presentation.base.TabComponent
import io.tujh.imago.presentation.theme.colors.ImagoColors

@Composable
fun BottomNavigationItem(modifier: Modifier = Modifier, tab: TabComponent<*, *, *>) {
    val navigator = LocalTabNavigator.current
    val isSelected = navigator.current == tab
    val title = tab.title
    val iconRes = tab.icon

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color by animateColorAsState(
            targetValue = if (isSelected) ImagoColors.red else Color.White
        )

        Icon(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { navigator.current = tab },
            contentDescription = null,
            painter = painterResource(iconRes),
            tint = color
        )

        Text(text = title, color = color, fontSize = 11.sp)
    }
}