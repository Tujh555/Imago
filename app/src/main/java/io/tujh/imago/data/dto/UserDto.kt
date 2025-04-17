package io.tujh.imago.data.dto

import com.google.gson.annotations.SerializedName
import io.tujh.imago.domain.user.User

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
) {
    companion object {
        val unknown = UserDto(
            id = "",
            avatar = null,
            name = "unknown",
            email = ""
        )
    }
}

fun UserDto.toDomain() = User(id, avatar, name, email)

fun User.toDto() = UserDto(id, avatar, name, email)