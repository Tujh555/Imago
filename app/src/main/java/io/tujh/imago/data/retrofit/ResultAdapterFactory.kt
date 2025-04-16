package io.tujh.imago.data.retrofit

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultAdapterFactory : CallAdapter.Factory() {
    override fun get(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(type)
        if (rawType != Call::class.java || type !is ParameterizedType) {
            return null
        }

        val responseType = getParameterUpperBound(0, type)
        val responseRawType = getRawType(responseType)

        if (responseRawType != Result::class.java || responseType !is ParameterizedType) {
            return null
        }
        val resultType = getParameterUpperBound(0, responseType)
        return ResultCallAdapter<Any>(resultType)
    }
}