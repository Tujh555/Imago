package io.tujh.imago.data.rest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.data.rest.auth.AuthApi
import io.tujh.imago.data.retrofit.ResultAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiProvider {
    @Provides
    @Singleton
    fun retrofit(tokenInterceptor: AuthTokenInterceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // TODO
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultAdapterFactory())
            .client(client)
            .build()
    }

    @Provides
    fun authApi(retrofit: Retrofit): AuthApi = retrofit.create()
}