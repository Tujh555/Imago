package io.tujh.imago.data.rest.auth

import com.google.gson.annotations.SerializedName
import io.tujh.imago.data.dto.UserDto

data class AuthResponse(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("token")
    val token: String
)
