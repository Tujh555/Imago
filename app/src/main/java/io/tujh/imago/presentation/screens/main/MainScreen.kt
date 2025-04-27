package io.tujh.imago.presentation.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import io.tujh.imago.presentation.components.BlurredBackground

class MainScreen : Screen {
    @Composable
    override fun Content() {
        BlurredBackground(Modifier.fillMaxSize()) {  }
    }
}