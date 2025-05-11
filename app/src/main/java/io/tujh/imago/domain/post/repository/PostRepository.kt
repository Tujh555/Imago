package io.tujh.imago.domain.post.repository

import android.net.Uri

interface PostRepository {
    suspend fun create(title: String, uris: List<Uri>): Result<Unit>

    suspend fun checkInFavorite(id: String): Result<Boolean>
}