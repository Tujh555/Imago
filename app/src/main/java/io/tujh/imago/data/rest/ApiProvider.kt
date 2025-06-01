package io.tujh.imago.data.rest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.data.rest.auth.AuthApi
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.data.rest.profile.ProfileApi
import io.tujh.imago.data.retrofit.ResultAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiProvider {
    @Provides
    @Singleton
    fun client(
        tokenInterceptor: AuthTokenInterceptor,
        notAuthorizedInterceptor: NotAuthorizedInterceptor,
        standSelectionInterceptor: StandSelectionInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder()
            .addInterceptor(standSelectionInterceptor)
            .addInterceptor(notAuthorizedInterceptor)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun retrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("http://0.0.0.0:8080/") // dummy url
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(ResultAdapterFactory())
        .client(client)
        .build()

    @Provides
    fun authApi(retrofit: Retrofit): AuthApi = retrofit.create()

    @Provides
    fun profileApi(retrofit: Retrofit): ProfileApi = retrofit.create()

    @Provides
    fun postApi(retrofit: Retrofit): PostApi = retrofit.create()
}