package io.tujh.imago.data.dto

import com.google.gson.annotations.SerializedName
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.utils.toInstantOrNow

class CommentDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("author")
    val author: UserDto,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("text")
    val text: String
)

fun CommentDto.toDomain() = Comment(
    id = id,
    author = author.toDomain(),
    createdAt = createdAt.toInstantOrNow(),
    text = text
)