package io.tujh.imago.data.repository.post

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.data.rest.post.FavoriteResponse
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.data.rest.post.RequestId
import io.tujh.imago.domain.post.repository.PostEditor

class PostEditorImpl @AssistedInject constructor(
    @Assisted private val postId: String,
    private val api: PostApi
) : PostEditor {
    @AssistedFactory
    interface Factory : PostEditor.Factory {
        override fun invoke(postId: String): PostEditorImpl
    }

    override suspend fun markFavorite() = api
        .addToFavorite(RequestId(postId))
        .map(FavoriteResponse::inFavorite)
}