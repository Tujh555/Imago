package io.tujh.imago.data.rest

import android.util.Log
import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.store.Store
import io.tujh.imago.domain.NotAuthorizedHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NotAuthorizedInterceptor @Inject constructor(
    private val handler: NotAuthorizedHandler,
    private val userStore: Store<UserDto>,
    private val tokenStore: Store<String>,
    private val scope: CoroutineScope
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code in arrayOf(401, 402)) {
            handler.handle()
            scope.launch {
                userStore.clear()
                tokenStore.clear()
            }
        }

        return response
    }
}