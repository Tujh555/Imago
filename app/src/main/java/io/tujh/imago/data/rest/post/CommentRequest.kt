package io.tujh.imago.data.rest.post

import com.google.gson.annotations.SerializedName

class CommentRequest(
    @SerializedName("post_id")
    val postId: String,
    @SerializedName("text")
    val text: String
)