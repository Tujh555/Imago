package io.tujh.imago.data.repository.post

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.data.dto.PostDto
import io.tujh.imago.data.dto.toDomain
import io.tujh.imago.data.repository.StateSource
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.domain.paging.source.PageableSource
import io.tujh.imago.domain.post.model.ShortPost
import io.tujh.imago.domain.post.repository.PostSource
import java.time.Instant

class ShortPostSource @AssistedInject constructor(
    @Assisted private val limit: @JvmSuppressWildcards Int,
    private val api: PostApi,
) : PostSource, PageableSource<Instant, ShortPost> by StateSource(
    getKey = ShortPost::createdAt,
    doFetch = { key -> api.short(limit, key.toString()).map { it.map(PostDto::toDomain) } },
) {
    @AssistedFactory
    interface Factory : PostSource.Factory {
        override fun invoke(limit: Int): ShortPostSource
    }
}