package io.tujh.imago.data.repository.post

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.data.dto.PostDto
import io.tujh.imago.data.dto.toDomain
import io.tujh.imago.data.repository.StateSource
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.domain.paging.source.PageableSource
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.post.repository.PostSource
import java.time.Instant

class AllSource @AssistedInject constructor(
    @Assisted private val limit: @JvmSuppressWildcards Int,
    api: PostApi,
) : PostSource, PageableSource<Instant, Post> by StateSource(
    getKey = Post::createdAt,
    doFetch = { key -> api.all(limit, key.toString()).map { it.map(PostDto::toDomain) } },
) {
    @AssistedFactory
    interface Factory : PostSource.AllFactory {
        override fun invoke(limit: Int): AllSource
    }
}

class OwnSource @AssistedInject constructor(
    @Assisted private val limit: @JvmSuppressWildcards Int,
    api: PostApi,
) : PostSource, PageableSource<Instant, Post> by StateSource(
    getKey = Post::createdAt,
    doFetch = { key -> api.my(limit, key.toString()).map { it.map(PostDto::toDomain) } },
) {
    @AssistedFactory
    interface Factory : PostSource.OwnFactory {
        override fun invoke(limit: Int): OwnSource
    }
}

class FavoritesSource @AssistedInject constructor(
    @Assisted private val limit: @JvmSuppressWildcards Int,
    api: PostApi,
) : PostSource, PageableSource<Instant, Post> by StateSource(
    getKey = Post::createdAt,
    doFetch = { key -> api.favorites(limit, key.toString()).map { it.map(PostDto::toDomain) } },
) {
    @AssistedFactory
    interface Factory : PostSource.FavoritesFactory {
        override fun invoke(limit: Int): FavoritesSource
    }
}