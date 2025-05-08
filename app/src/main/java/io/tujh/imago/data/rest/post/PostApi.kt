package io.tujh.imago.data.rest.post

import io.tujh.imago.data.dto.PostDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

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
        @Part images: List<MultipartBody.Part>
    ): Result<Unit>
}