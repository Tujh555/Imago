package io.tujh.imago.domain.post.repository

import io.tujh.imago.domain.paging.source.PageableSource
import io.tujh.imago.domain.post.model.Comment
import java.time.Instant

interface CommentsSource : PageableSource<Instant, Comment> {
    interface Factory {
        operator fun invoke(limit: Int, postId: String): CommentsSource
    }
}