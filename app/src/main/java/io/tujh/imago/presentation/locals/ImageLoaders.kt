package io.tujh.imago.presentation.locals

import androidx.compose.runtime.compositionLocalOf
import coil3.ImageLoader

val LocalFullImageLoader = compositionLocalOf<ImageLoader> {
    error("FullImageLoader not provided")
}

val LocalPreviewImageLoader = compositionLocalOf<ImageLoader> {
    error("PreviewImageLoader not provided")
}