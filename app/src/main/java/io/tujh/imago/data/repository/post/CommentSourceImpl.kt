package io.tujh.imago.data.repository.post

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.data.dto.CommentDto
import io.tujh.imago.data.dto.toDomain
import io.tujh.imago.data.repository.StateSource
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.domain.paging.source.PageableSource
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.post.repository.CommentsSource
import java.time.Instant

class CommentSourceImpl @AssistedInject constructor(
    @Assisted private val limit: Int,
    @Assisted private val postId: String,
    private val api: PostApi
) : CommentsSource, PageableSource<Instant, Comment> by StateSource(
    getKey = Comment::id,
    doFetch = { key ->
        api.comments(postId, limit, key.toString()).map { it.map(CommentDto::toDomain) }
    }
) {
    @AssistedFactory
    interface Factory : CommentsSource.Factory {
        override fun invoke(limit: Int, postId: String): CommentSourceImpl
    }
}