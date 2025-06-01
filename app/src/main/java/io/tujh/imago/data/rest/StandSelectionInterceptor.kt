package io.tujh.imago.data.rest

import android.util.Log
import io.tujh.imago.data.store.Store
import io.tujh.imago.data.utils.get
import io.tujh.imago.domain.ip.Stand
import io.tujh.imago.domain.ip.StandRepository
import io.tujh.imago.domain.utils.runBlockingWithCancellation
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class StandSelectionInterceptor @Inject constructor(
    private val store: Store<Stand>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val stand = runBlockingWithCancellation(chain.call()::isCanceled) {
            store.get().also { Log.d("--tag", "got = ${it?.value}") } ?: Stand.computeLocal()
        }
        val request = chain.request()

        val url = request.url.newBuilder().run {
            host(stand.host)
            stand.port.toIntOrNull()?.let(::port)
            build()
        }

        Log.d("--tag", "old = ${request.url}\nnew = $url")

        return request.newBuilder().url(url).build().let(chain::proceed)
    }
}