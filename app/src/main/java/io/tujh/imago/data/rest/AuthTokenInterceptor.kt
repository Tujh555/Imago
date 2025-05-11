package io.tujh.imago.data.rest

import io.tujh.imago.data.store.Store
import io.tujh.imago.domain.utils.runBlockingWithCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor(
    private val tokenStore: Store<String>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val token = runBlockingWithCancellation(chain.call()::isCanceled) {
            tokenStore.data.first()
        }

        if (token != null) {
            builder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}