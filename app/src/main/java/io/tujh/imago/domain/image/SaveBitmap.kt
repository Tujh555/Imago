package io.tujh.imago.domain.image

import android.graphics.Bitmap
import android.net.Uri
import io.tujh.imago.data.files.UriProvider
import javax.inject.Inject

class SaveBitmap @Inject constructor(
    private val uriProvider: UriProvider,
    private val saver: BitmapSaver
) {
    suspend operator fun invoke(bitmap: Bitmap): Uri {
        val uri = uriProvider.temporaryImage()
        saver(bitmap, uri)
        return uri
    }
}