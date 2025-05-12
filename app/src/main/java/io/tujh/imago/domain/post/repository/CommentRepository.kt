package io.tujh.imago.domain.post.repository

import io.tujh.imago.domain.post.model.StatusComment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    interface Factory : (String) -> CommentRepository

    fun observe(): Flow<List<StatusComment>>

    suspend fun send(text: String)

    fun clear()
}