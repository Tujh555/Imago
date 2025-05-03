package io.tujh.imago.data.dto

import com.google.gson.annotations.SerializedName
import io.tujh.imago.domain.post.model.PostImage
import io.tujh.imago.domain.post.model.ShortPost
import io.tujh.imago.domain.utils.toInstantOrNow

data class PostDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("first_image")
    val image: PostImageDto,
    @SerializedName("title")
    val title: String,
    @SerializedName("created_at")
    val createdAt: String,
)

data class PostImageDto(
    @SerializedName("preview_url")
    val previewUrl: String,
    @SerializedName("full_url")
    val fullUrl: String,
    @SerializedName("original_width")
    val originalWidth: Int,
    @SerializedName("original_height")
    val originalHeight: Int
)

fun PostImageDto.toDomain() = PostImage(previewUrl, fullUrl, originalWidth, originalHeight)

fun PostDto.toDomain() = ShortPost(
    id = id,
    firstImage = image.toDomain(),
    title = title,
    createdAt = createdAt.toInstantOrNow()
)