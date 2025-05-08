package io.tujh.imago.domain.post.uc

import io.tujh.imago.domain.paging.paginator.statePaginator
import io.tujh.imago.domain.paging.source.PageableSourcePager
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.post.repository.PostSource
import java.time.Instant
import javax.inject.Inject

class PostList @Inject constructor(
    private val allFactory: PostSource.AllFactory,
    private val ownFactory: PostSource.OwnFactory,
    private val favoritesFactory: PostSource.FavoritesFactory
) {
    private val limit = 30

    fun all() = create(allFactory)

    fun own() = create(ownFactory)

    fun favorite() = create(favoritesFactory)

    private fun create(factory: PostSource.Factory): PageableSourcePager<Post> {
        val source = factory(limit)
        val paginator = statePaginator(
            initialKey = Instant.now(),
            limit = limit,
            pageableSource = source,
            getKey = Post::createdAt
        )

        return PageableSourcePager(source, paginator)
    }
}