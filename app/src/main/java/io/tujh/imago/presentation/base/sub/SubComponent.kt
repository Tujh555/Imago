package io.tujh.imago.presentation.base.sub

import cafe.adriel.voyager.core.screen.Screen

interface SubComponent<out S> : Screen {
    val state: S
}