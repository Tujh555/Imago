package io.tujh.imago.data.rest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.data.dto.PostDto
import io.tujh.imago.data.dto.PostImageDto
import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.rest.auth.AuthApi
import io.tujh.imago.data.rest.auth.AuthRequest
import io.tujh.imago.data.rest.auth.AuthResponse
import io.tujh.imago.data.rest.auth.LogoutRequest
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.data.retrofit.ResultAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.time.Instant
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
        private val user = UserDto(
            id = UUID.randomUUID().toString(),
            avatar = null,
            name = "Test user",
            email = "test.user@mail.com"
        )

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

    @Provides
    fun postApi(retrofit: Retrofit): PostApi = object : PostApi {
        private val sizes = listOf(800 to 2500, 1920 to 1080, 2560 to 1440, 1920 to 1200, 3840 to 2160)
        private val images = sizes.flatMap { (w, h) ->
            testUrls(w, h).map { url ->
                PostImageDto(
                    url = url,
                    originalWidth = w,
                    originalHeight = h
                )
            }
        }

        private fun testUrls(w: Int, h: Int): List<String> {
            val size = "${w}x${h}"
            return listOf(
                "https://dummyimage.com/$size/ffffff/000000&text=$size-Black",
                "https://dummyimage.com/$size/ffffff/ff0000&text=$size-Red",
                "https://dummyimage.com/$size/ffffff/00ff00&text=$size-Green",
                "https://dummyimage.com/$size/ffffff/0000ff&text=$size-Blue",
                "https://dummyimage.com/$size/ffffff/ffff00&text=$size-Yellow",
            )
        }

        private fun nextPage(size: Int) = List(size) {
            PostDto(
                id = UUID.randomUUID().toString(),
                images = listOf(images[(size + it) % images.size]),
                title = "Title #${it}",
                createdAt = Instant.now().toString()
            )
        }
        private var shortCount = 0

        override suspend fun short(limit: Int, cursor: String): Result<List<PostDto>> {
            delay(1500)
            shortCount++
            if (shortCount == 5) {
                return nextPage(limit - 1).let { Result.success(it) }
            }

            return nextPage(limit).let { Result.success(it) }
        }

        override suspend fun my(limit: Int, cursor: String): Result<List<PostDto>> {
            TODO("Not yet implemented")
        }

        override suspend fun add(
            title: RequestBody,
            images: List<MultipartBody.Part>
        ): Result<Unit> {
            delay(50_000)
            return Result.success(Unit)
        }

    }
}