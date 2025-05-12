package io.tujh.imago.domain.post.repository

interface PostEditor {
    interface Factory {
        operator fun invoke(postId: String): PostEditor
    }

    suspend fun markFavorite(): Result<Boolean>
}