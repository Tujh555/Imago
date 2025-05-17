package io.tujh.imago.data.rest

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
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
import io.tujh.imago.data.rest.profile.AvatarUpdateResponse
import io.tujh.imago.data.rest.profile.ProfileApi
import io.tujh.imago.data.retrofit.ResultAdapterFactory
import io.tujh.imago.domain.user.CurrentUser
import io.tujh.imago.presentation.components.uuidIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton
import kotlin.random.Random.Default.nextInt

private val fm = ConcurrentHashMap<String, Boolean>()

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
    }

    @Provides
    fun profileApi(retrofit: Retrofit): ProfileApi = object : ProfileApi {
        override suspend fun upload(file: MultipartBody.Part): Result<AvatarUpdateResponse> {
            delay(10000)
            return Result.success(
                AvatarUpdateResponse(
                    "file:///android_asset/avatar_${nextInt(1, 50)}.webp"
                )
            )
        }

        override suspend fun changeName(name: String): Result<Unit> {
            delay(1500)
            return Result.success(Unit)
        }
    }

    @Provides
    fun postApi(retrofit: Retrofit, currentUser: CurrentUser): PostApi = object : PostApi {
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
            ),
            PostImageDto(
                url = "https://image-processor-storage.s3.us-west-2.amazonaws.com/images/281c2d4581ed27c8a258b0e79bc504ad/halo-of-neon-ring-illuminated-in-the-stunning-landscape-of-yosemite.jpg",
                originalWidth = 1400, originalHeight = 933
            ),
            PostImageDto(
                url = "https://bkacontent.com/wp-content/uploads/2016/06/Depositphotos_31146757_l-2015.jpg",
                originalWidth = 1920, originalHeight = 1752
            ),
            PostImageDto(
                url = "https://static01.nytimes.com/newsgraphics/2024-12-06-disinfo-ai-inserts/a0ffc5e3-43da-426b-85eb-db663e12de7b/_assets/field.jpg",
                originalWidth = 1200, originalHeight = 1491
            ),
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
            sizes: RequestBody,
            images: List<MultipartBody.Part>
        ): Result<Unit> {
            delay(50_000)
            return Result.success(Unit)
        }

        override suspend fun addToFavorite(body: RequestId): Result<FavoriteResponse> {
            delay(1500)
            val res = (fm[body.id] ?: false).not()
            fm[body.id] = res
            return Result.success(FavoriteResponse(res))
        }

        override suspend fun checkInFavorite(body: RequestId): Result<FavoriteResponse> {
            delay(1000)
            return Result.success(FavoriteResponse(fm[body.id] ?: false))
        }

        private val users = List(20) {
            UserDto(
                id = UUID.randomUUID().toString(),
                avatar = null,
                name = "Name #${it}",
                email = "email #${it}"
            )
        }
        private fun commentsPage(size: Int) = List(size) {
            CommentDto(
                id = UUID.randomUUID().toString(),
                author = users.random(),
                createdAt = Instant.now().toString(),
                text = LoremIpsum(nextInt(10, 20)).values.joinToString(" ")
            )
        }

        private var commentCnt = 0
        override suspend fun comments(
            postId: String,
            limit: Int,
            cursor: String
        ): Result<List<CommentDto>> {
            delay(1000)
            commentCnt++

            if (commentCnt == 5) {
                return commentsPage(limit - 1).let { Result.success(it) }
            }

            return commentsPage(limit).let { Result.success(it) }
        }

        private val comReqCnt =  AtomicInteger(0)
        override suspend fun comment(body: CommentRequest): Result<CommentDto> {
            delay(3500)
            if (comReqCnt.getAndIncrement() % 3 == 0) {
                return Result.failure(RuntimeException())
            }

            val u = currentUser.filterNotNull().first()
            val c = CommentDto(
                id = UUID.randomUUID().toString(),
                author = UserDto(
                    id = u.id,
                    avatar = u.avatar,
                    name = u.name,
                    email = u.email
                ),
                createdAt = Instant.now().toString(),
                text = body.text
            )

            return Result.success(c)
        }
    }
}