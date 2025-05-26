package io.tujh.imago.data.image

import android.content.Context
import android.util.Log
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.allowConversionToBitmap
import coil3.request.allowHardware
import coil3.request.allowRgb565
import coil3.request.crossfade
import coil3.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.data.utils.ScreenSizeInterceptor
import javax.inject.Inject

class GetFullImageLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val maxDiskSize = 2L * 1024 * 1024 * 1024 // 2GB

    operator fun invoke(): ImageLoader {
        val memory = MemoryCache
            .Builder()
            .maxSizePercent(context, 0.4)
            .build()
        val disk = DiskCache
            .Builder()
            .directory(context.filesDir)
            .maxSizePercent(0.5)
            .maximumMaxSizeBytes(maxDiskSize)
            .build()

        return ImageLoader
            .Builder(context)
            .components { add(ScreenSizeInterceptor(context)) }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCache(memory)
            .diskCache(disk)
            .allowHardware(true)
            .allowRgb565(false)
            .allowConversionToBitmap(true)
            .crossfade(true)
            .logger(object : Logger {
                override var minLevel = Logger.Level.Debug

                override fun log(
                    tag: String,
                    level: Logger.Level,
                    message: String?,
                    throwable: Throwable?
                ) {
                    if (throwable == null) {
                        Log.d("--tag", message.orEmpty())
                    } else {
                        Log.e("--tag", message, throwable)
                    }
                }

            })
            .build()
    }
}