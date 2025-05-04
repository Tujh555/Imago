package io.tujh.imago.data.image

import android.content.Context
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.data.utils.ScreenSizeInterceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFullImageLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(): ImageLoader {
        val memory = MemoryCache.Builder().maxSizePercent(context, 0.4)
        return ImageLoader
            .Builder(context)
            .components { add(ScreenSizeInterceptor(context)) }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCache(memory::build)
            .crossfade(true)
            .logger(CoilLogger)
            .build()
    }
}