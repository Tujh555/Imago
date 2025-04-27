package io.tujh.imago.data.rest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.rest.auth.AuthApi
import io.tujh.imago.data.rest.auth.AuthRequest
import io.tujh.imago.data.rest.auth.AuthResponse
import io.tujh.imago.data.rest.auth.LogoutRequest
import io.tujh.imago.data.retrofit.ResultAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.UUID
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
    fun authApi(retrofit: Retrofit): AuthApi = object : AuthApi {
        private val user = UserDto(id = UUID.randomUUID().toString(), avatar = null, name = "Test user", email = "test.user@mail.com")
        override suspend fun signIn(body: AuthRequest): Result<AuthResponse> {
            delay(500)
            return Result.success(
                AuthResponse(user = user, token = UUID.randomUUID().toString())
            )
        }

        override suspend fun signUp(body: AuthRequest): Result<AuthResponse> {
            delay(500)
            return Result.success(
                AuthResponse(user = user, token = UUID.randomUUID().toString())
            )
        }

        override suspend fun logout(request: LogoutRequest): Result<Unit> {
            delay(500)
            return Result.success(Unit)
        }
    }
}