package io.tujh.imago.presentation.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.hilt.getScreenModel
import io.tujh.imago.presentation.base.StateComponent

class SplashScreen : StateComponent<Nothing, Boolean?> {
    @Composable
    override fun Content(state: Boolean?, onAction: (Nothing) -> Unit) {
        Box(modifier = Modifier.fillMaxSize())
    }

    @Composable
    override fun model(): SplashModel = getScreenModel()
}