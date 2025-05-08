package io.tujh.imago.data.repository.post

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.data.dto.CommentDto
import io.tujh.imago.data.dto.toDomain
import io.tujh.imago.data.rest.post.CommentRequest
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.data.rest.post.RequestId
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.post.model.Post
import io.tujh.imago.domain.post.repository.PostEditor

class PostEditorImpl @AssistedInject constructor(
    @Assisted private val post: Post,
    private val api: PostApi
) : PostEditor {
    @AssistedFactory
    interface Factory : PostEditor.Factory {
        override fun invoke(post: Post): PostEditorImpl
    }

    override suspend fun markFavorite() = api.addToFavorite(RequestId(post.id))

    override suspend fun comment(text: String): Result<Comment> {
        val body = CommentRequest(post.id, text)
        return api.comment(body).map(CommentDto::toDomain)
    }
}