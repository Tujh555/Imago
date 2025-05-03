package io.tujh.imago.domain.post.uc

import io.tujh.imago.domain.paging.paginator.statePaginator
import io.tujh.imago.domain.paging.source.PageableSourcePager
import io.tujh.imago.domain.post.model.ShortPost
import io.tujh.imago.domain.post.repository.PostSource
import java.time.Instant
import javax.inject.Inject

class ShortPostPager @Inject constructor(
    private val sourceFactory: PostSource.Factory
) {
    private val limit = 30

    operator fun invoke(): PageableSourcePager<ShortPost> {
        val source = sourceFactory(limit)
        val paginator = statePaginator(
            initialKey = Instant.now(),
            limit = limit,
            pageableSource = source,
            getKey = ShortPost::createdAt
        )

        return PageableSourcePager(source, paginator)
    }
}