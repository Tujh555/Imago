package io.tujh.imago.data.image

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.disk.DiskCache

import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.data.utils.ScreenSizeInterceptor
import kotlinx.coroutines.delay
import okhttp3.Interceptor
import okio.FileSystem
import javax.inject.Inject

class GetFullImageLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val maxDiskSize = 2L * 1024 * 1024 * 1024 // 2GB

    operator fun invoke(): ImageLoader {
        val disk = DiskCache
            .Builder()
            .directory(context.filesDir)
            .maxSizePercent(0.5)
            .maximumMaxSizeBytes(maxDiskSize)
        //val memory = MemoryCache.Builder().maxSizePercent(context, 0.2)

        return ImageLoader
            .Builder(context)
            .components {
                add(ScreenSizeInterceptor(context))
            }
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            //.memoryCache(memory::build)
            .crossfade(true)
            .diskCache(disk::build)
            .logger(object : Logger {
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
            })
            .build()
    }
}