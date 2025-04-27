package io.tujh.imago.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.tujh.imago.R
import io.tujh.imago.presentation.base.StateComponent
import io.tujh.imago.presentation.screens.main.MainScreen
import io.tujh.imago.presentation.screens.signin.SignInScreen

class SplashScreen : StateComponent<Nothing, Boolean?> {
    @Composable
    override fun Content(state: Boolean?, onAction: (Nothing) -> Unit) {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val angle = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                angle.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 1000, easing = EaseInOutBack)
                )
            }

            Icon(
                modifier = Modifier.size(512.dp).rotate(angle.value),
                painter = painterResource(R.drawable.ic_launcher_atom_foreground),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.size(20.dp))
            Text(text = "Imago", fontSize = 57.sp, color = MaterialTheme.colorScheme.onSurface)
        }

        LaunchedEffect(state) {
            when (state) {
                true -> navigator.replace(MainScreen())
                false -> navigator.replace(SignInScreen())
                null -> Unit
            }
        }
    }

    @Composable
    override fun model(): SplashModel = getScreenModel()
}