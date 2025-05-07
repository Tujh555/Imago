package io.tujh.imago.data.dto

import com.google.gson.annotations.SerializedName
import io.tujh.imago.domain.post.model.PostImage
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.utils.toInstantOrNow

data class PostDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("images")
    val images: List<PostImageDto>,
    @SerializedName("title")
    val title: String,
    @SerializedName("created_at")
    val createdAt: String,
)

data class PostImageDto(
    @SerializedName("url")
    val url: String,
    @SerializedName("original_width")
    val originalWidth: Int,
    @SerializedName("original_height")
    val originalHeight: Int
)

fun PostImageDto.toDomain() = PostImage(url, originalWidth, originalHeight)

fun PostDto.toDomain() = Post(
    id = id,
    images = images.map(PostImageDto::toDomain),
    title = title,
    createdAt = createdAt.toInstantOrNow()
)