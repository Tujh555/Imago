package io.tujh.imago.data.retrofit

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResultCallAdapter<T : Any>(private val responseType: Type) : CallAdapter<T, Call<Result<T>>> {
    override fun responseType() = responseType

    override fun adapt(p0: Call<T>) = ResultCall(p0)
}