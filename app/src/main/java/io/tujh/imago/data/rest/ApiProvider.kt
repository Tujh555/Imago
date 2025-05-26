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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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
    fun retrofit(
        tokenInterceptor: AuthTokenInterceptor,
        notAuthorizedInterceptor: NotAuthorizedInterceptor
    ): Retrofit {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor(notAuthorizedInterceptor)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(logging)
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

    @Provides
    fun profileApi(retrofit: Retrofit): ProfileApi = retrofit.create()

    @Provides
    fun postApi(retrofit: Retrofit): PostApi = retrofit.create()
}