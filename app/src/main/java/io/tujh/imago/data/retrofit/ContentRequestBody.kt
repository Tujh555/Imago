package io.tujh.imago.data.retrofit

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.BufferedSource
import okio.FileNotFoundException
import okio.buffer
import okio.source

private class ContentRequestBody(private val source: () -> BufferedSource) : RequestBody() {
    override fun contentLength() = -1L

    override fun contentType() = null

    override fun writeTo(sink: BufferedSink) {
        source().use(sink::writeAll)
    }
}

fun Context.formDataOf(name: String, uri: Uri): MultipartBody.Part {
    val resolver = contentResolver
    val body = ContentRequestBody { resolver.openOrThrow(uri).source().buffer() }
    return MultipartBody.Part.createFormData(name, null, body)
}

fun ContentResolver.openOrThrow(uri: Uri) =
    openInputStream(uri) ?: throw FileNotFoundException(uri.toString())