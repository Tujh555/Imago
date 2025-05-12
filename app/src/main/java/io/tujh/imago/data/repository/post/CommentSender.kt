package io.tujh.imago.data.repository.post

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.data.dto.CommentDto
import io.tujh.imago.data.dto.toDomain
import io.tujh.imago.data.rest.post.CommentRequest
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.post.model.StatusComment
import io.tujh.imago.domain.post.repository.CommentRepository
import io.tujh.imago.domain.user.CurrentUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.util.UUID

class CommentSender @AssistedInject constructor(
    @Assisted private val postId: String,
    private val api: PostApi,
    private val currentUser: CurrentUser
) : CommentRepository {
    @AssistedFactory
    interface Factory : CommentRepository.Factory {
        override fun invoke(p1: String): CommentSender
    }

    private val sendingComments = MutableStateFlow(emptyList<StatusComment>())

    override fun observe() = sendingComments

    override suspend fun send(text: String) {
        val user = currentUser.filterNotNull().first()
        val id = UUID.randomUUID().toString()
        val comment = StatusComment(
            comment = Comment(
                id = id,
                author = user,
                createdAt = Instant.now(),
                text = text
            ),
            status = Comment.Status.Sending
        )
        sendingComments.update { comments ->
            buildList(comments.size + 1) {
                add(comment)
                addAll(comments)
            }
        }

        api
            .comment(CommentRequest(postId, text))
            .map(CommentDto::toDomain)
            .onSuccess { newComment ->
                sendingComments.update { comments ->
                    comments.map {
                        if (it.comment.id == id) {
                            StatusComment(newComment, Comment.Status.Empty)
                        } else {
                            it
                        }
                    }
                }
            }
            .onFailure {
                sendingComments.update { comments ->
                    comments.map {
                        if (it.comment.id == id) {
                            it.copy(status = Comment.Status.Error)
                        } else {
                            it
                        }
                    }
                }
            }
    }

    override fun clear() {
        sendingComments.value = emptyList()
    }
}