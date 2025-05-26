package io.tujh.imago.data.retrofit

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.BufferedSource
import okio.FileNotFoundException
import okio.buffer
import okio.source

private class ContentRequestBody(
    private val type: MediaType? = null,
    private val size: Long? = null,
    private val source: () -> BufferedSource
) : RequestBody() {
    override fun contentLength() = size ?: -1L

    override fun contentType() = type

    override fun writeTo(sink: BufferedSink) {
        source().use(sink::writeAll)
    }
}

fun Context.formDataOf(name: String, uri: Uri): MultipartBody.Part {
    val resolver = contentResolver
    val (fileName, size, type) = resolver.meta(uri)
    val body = ContentRequestBody(type?.toMediaTypeOrNull(), size) {
        resolver.openOrThrow(uri).source().buffer()
    }
    return MultipartBody.Part.createFormData(name, fileName, body)
}

private fun ContentResolver.meta(uri: Uri): Triple<String?, Long?, String?> {
    val error: Triple<String?, Long?, String?> = Triple(null, null, null)

    val result = runCatching {
        query(uri, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            val name = cursor
                .getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                .let(cursor::getStringOrNull)
            val size = cursor
                .getColumnIndexOrThrow(OpenableColumns.SIZE)
                .let(cursor::getLongOrNull)

            Triple(name, size, getType(uri))
        }
    }

    return result.getOrDefault(error) ?: error
}

fun ContentResolver.openOrThrow(uri: Uri) =
    openInputStream(uri) ?: throw FileNotFoundException(uri.toString())