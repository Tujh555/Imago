package io.tujh.imago.domain.image

import android.graphics.Bitmap
import android.net.Uri

interface BitmapLoader {
    suspend fun load(): Bitmap?

    interface Factory : (Uri) -> BitmapLoader
}