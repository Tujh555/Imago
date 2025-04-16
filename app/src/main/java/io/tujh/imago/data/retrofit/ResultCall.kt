package io.tujh.imago.data.retrofit

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ResultCall<T : Any>(private val delegate: Call<T>) : Call<Result<T>> {
    override fun enqueue(callback: Callback<Result<T>>) {
        val callbackProxy = object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val mapped = try {
                    if (response.isSuccessful) {
                        Response.success(
                            response.code(),
                            Result.success(response.body()!!)
                        )
                    } else {
                        Response.success(Result.failure(HttpException(response)))
                    }
                } catch (e: Exception) {
                    Response.success(Result.failure(e))
                }
                callback.onResponse(this@ResultCall, mapped)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@ResultCall, Response.success(Result.failure(t)))
            }

        }
        delegate.enqueue(callbackProxy)
    }

    override fun clone() = ResultCall(delegate.clone())

    override fun execute() = error("Unsupported")

    override fun isExecuted() = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled() = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

}