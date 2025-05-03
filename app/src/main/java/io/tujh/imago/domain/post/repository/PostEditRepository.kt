package io.tujh.imago.domain.post.repository

import android.net.Uri

interface PostEditRepository {
    interface Factory : (String) -> PostEditRepository

    suspend fun addImage(uri: Uri): Result<Unit>

    suspend fun editTitle(title: String): Result<Unit>

    suspend fun removeImage(url: String): Result<Unit>
}