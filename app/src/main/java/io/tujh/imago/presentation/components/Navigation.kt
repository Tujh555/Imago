@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.tujh.imago.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.Navigator

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope> {
    error("SharedTransitionScope is not provided")
}

val LocalSharedNavVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope> {
    error("SharedNavAnimationVisibilityScope is not provided")
}