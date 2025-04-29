package io.tujh.imago.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.TabNavigator
import io.tujh.imago.R
import io.tujh.imago.presentation.base.Model
import io.tujh.imago.presentation.base.TabComponent
import io.tujh.imago.presentation.theme.colors.ImagoColors
import io.tujh.imago.presentation.theme.colors.ImagoTheme

val brush = Brush.verticalGradient(
    0f to ImagoColors.semitransparent.copy(alpha = 0f),
    1f to ImagoColors.semitransparent.copy(alpha = 0.2f),
)

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    fabSize: Dp = 50.dp,
    tabs: List<TabComponent<*, *, *>>,
    onFabClick: () -> Unit
) {
    Column(modifier) {
        Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(brush).blur(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val half = tabs.size / 2
            for (i in 0..< half) {
                val tab = tabs[i]
                key(tab.key) {
                    BottomNavigationItem(Modifier.height(fabSize), tab)
                }
            }

            Box(
                modifier = Modifier
                    .size(fabSize)
                    .clip(CircleShape)
                    .background(ImagoColors.red, CircleShape)
                    .clickable(onClick = onFabClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }

            for (i in half..< tabs.size) {
                val tab = tabs[i]
                key(tab.key) {
                    BottomNavigationItem(Modifier.height(fabSize), tab)
                }
            }
        }
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    ImagoTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            val tabs = remember {
                listOf(
                    object : TabComponent<Any, Any, Any> {
                        override val key: ScreenKey = uniqueScreenKey
                        override val title: String = "Home"
                        override val icon: Int = R.drawable.ic_home

                        @Composable
                        override fun Content(state: Any, onAction: (Any) -> Unit) = Unit

                        @Composable
                        override fun Event(event: Any) = Unit

                        @Composable
                        override fun model(): Model<Any, Any, Any> = error("")
                    },
                    object : TabComponent<Any, Any, Any> {
                        override val key: ScreenKey = uniqueScreenKey
                        override val title: String = "Profile"
                        override val icon: Int = R.drawable.ic_profile

                        @Composable
                        override fun Content(state: Any, onAction: (Any) -> Unit) = Unit

                        @Composable
                        override fun Event(event: Any) = Unit

                        @Composable
                        override fun model(): Model<Any, Any, Any> = error("")
                    }
                )
            }

            TabNavigator(tabs.first()) {
                BottomNavigationBar(tabs = tabs) { }
            }
        }
    }
}