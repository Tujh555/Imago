package io.tujh.imago.data.image

import android.util.Log
import coil3.util.Logger

object CoilLogger: Logger {
    override var minLevel: Logger.Level = Logger.Level.Debug

    override fun log(
        tag: String,
        level: Logger.Level,
        message: String?,
        throwable: Throwable?
    ) {
        if (throwable != null) {
            Log.e("--tag", message, throwable)
        } else {
            Log.d("--tag", message.orEmpty())
        }
    }
}