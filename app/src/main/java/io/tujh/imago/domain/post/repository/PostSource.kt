package io.tujh.imago.domain.post.repository

import io.tujh.imago.domain.paging.source.PageableSource
import io.tujh.imago.domain.post.model.ShortPost
import java.time.Instant

interface PostSource : PageableSource<Instant, ShortPost> {
    interface Factory {
        operator fun invoke(limit: Int) : PostSource
    }
}