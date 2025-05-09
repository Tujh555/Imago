package io.tujh.imago.domain.post.repository

import io.tujh.imago.domain.post.model.Comment

interface PostEditor {
    interface Factory {
        operator fun invoke(postId: String): PostEditor
    }

    suspend fun markFavorite(): Result<Boolean>

    suspend fun comment(text: String): Result<Comment>
}