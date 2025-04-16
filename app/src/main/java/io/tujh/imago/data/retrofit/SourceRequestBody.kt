package io.tujh.imago.data.retrofit

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.InputStream

fun InputStream.toRequestBody(
    type: MediaType? = null,
    size: Long? = null
): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType? = type
    override fun contentLength(): Long = size ?: -1

    override fun writeTo(sink: BufferedSink) {
        buffered().source().use(sink::writeAll)
    }
}