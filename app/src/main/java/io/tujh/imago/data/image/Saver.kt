package io.tujh.imago.data.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.domain.image.BitmapSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Saver @Inject constructor(
    @ApplicationContext private val context: Context,
) : BitmapSaver {
    override suspend fun invoke(p1: Bitmap, p2: Uri): Unit = withContext(Dispatchers.IO) {
        val stream = context.contentResolver
            .openOutputStream(p2)
            ?: error("Uri $p2 not available for writing ")

        stream.use { out -> p1.compress(Bitmap.CompressFormat.JPEG, 100, out) }
    }
}