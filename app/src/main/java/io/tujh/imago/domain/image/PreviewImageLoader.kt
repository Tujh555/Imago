package io.tujh.imago.domain.image

import coil3.ImageLoader

interface PreviewImageLoader : ImageLoader

fun PreviewImageLoader(underlying: ImageLoader): PreviewImageLoader =
    object : PreviewImageLoader, ImageLoader by underlying {}