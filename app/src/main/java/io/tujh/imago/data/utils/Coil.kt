package io.tujh.imago.data.utils

import android.content.Context
import coil3.intercept.Interceptor
import coil3.size.Size

@Suppress("FunctionName")
fun ScreenSizeInterceptor(context: Context) = Interceptor { chain ->
    val maxSide = context.resources.displayMetrics.run { maxOf(widthPixels, heightPixels) }
    val screenSize = Size(maxSide, maxSide)
    chain.withSize(screenSize).proceed()
}