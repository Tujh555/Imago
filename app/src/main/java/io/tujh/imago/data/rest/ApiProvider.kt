package io.tujh.imago.data.rest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tujh.imago.data.dto.CommentDto
import io.tujh.imago.data.dto.PostDto
import io.tujh.imago.data.dto.PostImageDto
import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.rest.auth.AuthApi
import io.tujh.imago.data.rest.auth.AuthRequest
import io.tujh.imago.data.rest.auth.AuthResponse
import io.tujh.imago.data.rest.auth.LogoutRequest
import io.tujh.imago.data.rest.post.CommentRequest
import io.tujh.imago.data.rest.post.FavoriteResponse
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.data.rest.post.RequestId
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
import java.util.concurrent.ConcurrentHashMap
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
        private val another = listOf(
            PostImageDto(
                url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSNAkB1j2W0ejEMyWFYmTpvMoKYCzy99XwD_Q&s",
                originalWidth = 10212, originalHeight = 6806
            ),
            PostImageDto(
                url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQJxo2NFiYcR35GzCk5T3nxA7rGlSsXvIfJwg&s",
                originalWidth = 1280, originalHeight = 720
            ),
            PostImageDto(
                url = "https://static.vecteezy.com/system/resources/thumbnails/036/324/708/small/ai-generated-picture-of-a-tiger-walking-in-the-forest-photo.jpg",
                originalWidth = 300, originalHeight = 200
            ),
            PostImageDto(
                url = "https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630",
                originalWidth = 1200, originalHeight = 630
            ),
            PostImageDto(
                url = "https://img.freepik.com/free-photo/closeup-scarlet-macaw-from-side-view-scarlet-macaw-closeup-head_488145-3540.jpg?semt=ais_hybrid&w=740",
                originalWidth = 740, originalHeight = 1109
            )
        )
        private val images = another + sizes.flatMap { (w, h) ->
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
                "https://dummyimage.com/$size/000000/000000&text=$size-Black",
                "https://dummyimage.com/$size/000000/ff0000&text=$size-Red",
                "https://dummyimage.com/$size/000000/00ff00&text=$size-Green",
                "https://dummyimage.com/$size/000000/0000ff&text=$size-Blue",
                "https://dummyimage.com/$size/000000/ffff00&text=$size-Yellow",
            )
        }

        private fun nextPage(size: Int) = List(size) {
            PostDto(
                id = UUID.randomUUID().toString(),
                images = List(10) { i -> images[(size + i + it) % images.size] },
                title = "Title #${it}",
                createdAt = Instant.now().toString()
            )
        }
        private var allCnt = 0
        private var myCnt = 0
        private var favCnt = 0

        override suspend fun all(limit: Int, cursor: String): Result<List<PostDto>> {
            delay(1500)
            allCnt++
            if (allCnt == 5) {
                return nextPage(limit - 1).let { Result.success(it) }
            }

            return nextPage(limit).let { Result.success(it) }
        }

        override suspend fun my(limit: Int, cursor: String): Result<List<PostDto>> {
            delay(1500)
            myCnt++
            if (myCnt == 5) {
                return nextPage(limit - 1).let { Result.success(it) }
            }

            return nextPage(limit).let { Result.success(it) }
        }

        override suspend fun favorites(limit: Int, cursor: String): Result<List<PostDto>> {
            delay(1500)
            favCnt++
            if (favCnt == 5) {
                return nextPage(limit - 1).let { Result.success(it) }
            }

            return nextPage(limit).let { Result.success(it) }
        }

        override suspend fun add(
            title: RequestBody,
            images: List<MultipartBody.Part>
        ): Result<Unit> {
            delay(50_000)
            return Result.success(Unit)
        }

        private val fm = ConcurrentHashMap<String, Boolean>()
        override suspend fun addToFavorite(body: RequestId): Result<FavoriteResponse> {
            delay(1500)
            val res = (fm[body.id] ?: false).not()
            fm[body.id] = res
            return Result.success(FavoriteResponse(res))
        }

        override suspend fun comments(
            postId: String,
            limit: Int,
            cursor: String
        ): Result<List<CommentDto>> {
            TODO("Not yet implemented")
        }

        override suspend fun comment(body: CommentRequest): Result<CommentDto> {
            TODO("Not yet implemented")
        }
    }
}