package io.tujh.imago.domain.post.repository

import io.tujh.imago.domain.paging.source.PageableSource
import io.tujh.imago.domain.post.model.Post
import java.time.Instant

interface PostSource : PageableSource<Instant, Post> {
    interface Factory {
        operator fun invoke(limit: Int): PostSource
    }

    interface AllFactory : Factory
    interface OwnFactory : Factory
    interface FavoritesFactory : Factory
}