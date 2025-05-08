package io.tujh.imago.domain.post.uc

import io.tujh.imago.domain.paging.paginator.statePaginator
import io.tujh.imago.domain.paging.source.PageableSourcePager
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.post.repository.CommentsSource
import java.time.Instant
import javax.inject.Inject

class Comments @Inject constructor(
    private val sourceFactory: CommentsSource.Factory
) {
    private val limit = 30

    operator fun invoke(postId: String): PageableSourcePager<Comment> {
        val source = sourceFactory(limit, postId)
        val paginator = statePaginator(Instant.now(), limit, source, Comment::createdAt)
        return PageableSourcePager(source, paginator)
    }
}