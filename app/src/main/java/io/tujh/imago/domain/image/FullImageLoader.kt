package io.tujh.imago.domain.image

import coil3.ImageLoader

interface FullImageLoader : ImageLoader

fun FullImageLoader(underlying: ImageLoader): FullImageLoader =
    object : FullImageLoader, ImageLoader by underlying {}