package io.tujh.imago.domain.post.repository

import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.post.model.Post

interface PostEditor {
    interface Factory {
        operator fun invoke(post: Post): PostEditor
    }

    suspend fun markFavorite(): Result<Unit>

    suspend fun comment(text: String): Result<Comment>
}