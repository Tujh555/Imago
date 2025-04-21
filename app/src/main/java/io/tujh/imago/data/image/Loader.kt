package io.tujh.imago.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.domain.image.BitmapLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.use
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max

class Loader @AssistedInject constructor(
    @Assisted private val uri: Uri,
    @ApplicationContext context: Context
) : BitmapLoader {
    private val maxSide = 1920
    private val resolver = context.contentResolver

    @AssistedFactory
    interface Factory : BitmapLoader.Factory {
        override fun invoke(p1: Uri): Loader
    }

    override suspend fun load() = withContext(Dispatchers.IO) {
        runCatching { uri.decode()?.rotateIfNeed(uri) }.getOrNull()
    }

    private fun Bitmap.rotateIfNeed(uri: Uri): Bitmap? {
        fun rotate(angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }

        val stream = resolver.openInputStream(uri) ?: return null
        val exif = ExifInterface(stream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(270f)
            else -> this
        }
    }

    private fun Uri.decode(): Bitmap? {
        val stream = resolver.openInputStream(this) ?: return null
        val realMaxSide = stream.use { input ->
            val options = options { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, options)
            options.run { max(outWidth, outHeight) }
        }
        val resampledStream = resolver.openInputStream(this) ?: return null
        val sample = calculateInSampleSize(realMaxSide)

        return resampledStream.use { input ->
            val resampledOptions = options { inSampleSize = sample }
            BitmapFactory.decodeStream(input, null, resampledOptions)
        }
    }

    private fun calculateInSampleSize(real: Int): Int {
        val ratio = real.toFloat() / maxSide

        if (ratio <= 2f) return 2

        val degree = ceil(log2(ratio)).toInt()

        return 1 shl degree
    }

    private inline fun options(block: BitmapFactory.Options.() -> Unit) =
        BitmapFactory.Options().apply(block)
}