package io.tujh.imago.data.rest.post

import io.tujh.imago.data.dto.CommentDto
import io.tujh.imago.data.dto.PostDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.ConcurrentHashMap

interface PostApi {
    @GET("/posts/all")
    suspend fun all(
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String,
    ): Result<List<PostDto>>

    @GET("/posts/my")
    suspend fun my(
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String,
    ): Result<List<PostDto>>

    @GET("/posts/favorite")
    suspend fun favorites(
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String,
    ): Result<List<PostDto>>

    @Multipart
    @POST("/posts/add")
    suspend fun add(
        @Part("title") title: RequestBody,
        @Part("sizes") sizes: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Result<Unit>

    @POST("/posts/favorites/add")
    suspend fun addToFavorite(@Body body: RequestId): Result<FavoriteResponse>

    @GET("/posts/favorites/check")
    suspend fun checkInFavorite(@Body body: RequestId): Result<FavoriteResponse>

    @GET("/posts/comments")
    suspend fun comments(
        @Query("postId") postId: String,
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String,
    ): Result<List<CommentDto>>

    @POST("/posts/comments/add")
    suspend fun comment(@Body body: CommentRequest): Result<CommentDto>
}