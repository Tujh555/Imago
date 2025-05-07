package io.tujh.imago.domain.post.uc

import io.tujh.imago.domain.paging.paginator.statePaginator
import io.tujh.imago.domain.paging.source.PageableSourcePager
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.post.repository.PostSource
import java.time.Instant
import javax.inject.Inject

class AllPostPager @Inject constructor(
    private val sourceFactory: PostSource.Factory
) {
    private val limit = 30

    operator fun invoke(): PageableSourcePager<Post> {
        val source = sourceFactory(limit)
        val paginator = statePaginator(
            initialKey = Instant.now(),
            limit = limit,
            pageableSource = source,
            getKey = Post::createdAt
        )

        return PageableSourcePager(source, paginator)
    }
}