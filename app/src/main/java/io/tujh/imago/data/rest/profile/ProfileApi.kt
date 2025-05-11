package io.tujh.imago.data.rest.profile

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApi {
    @Multipart
    @POST("user/avatar")
    suspend fun upload(@Part file: MultipartBody.Part): Result<AvatarUpdateResponse>

    @PATCH("user/name")
    suspend fun changeName(name: String): Result<Unit>
}