package io.tujh.imago.domain.post.uc

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.paging.paginator.statePaginator
import io.tujh.imago.domain.paging.source.PageableSourcePager
import io.tujh.imago.domain.paging.source.PagedElements
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.post.model.StatusComment
import io.tujh.imago.domain.post.repository.CommentsSource
import io.tujh.imago.domain.post.repository.PostEditor
import io.tujh.imago.domain.user.CurrentUser
import io.tujh.imago.domain.utils.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.util.UUID

class Comments @AssistedInject constructor(
    @Assisted private val postId: String,
    sourceFactory: CommentsSource.Factory,
    editorFactory: PostEditor.Factory,
    private val currentUser: CurrentUser,
) {
    @AssistedFactory
    interface Factory : (String) -> Comments

    private val limit = 30
    private val editor = editorFactory(postId)
    private val source = sourceFactory(limit, postId)
    private val paginator = statePaginator(Instant.now(), limit, source, Comment::createdAt)
    private val sendingComments = MutableStateFlow<List<StatusComment>>(emptyList())
    private val _pager = PageableSourcePager(source, paginator)

    val pager: PageableSourcePager<StatusComment> = object : PageableSourcePager<StatusComment> {
        override fun paginate(position: Flow<Int>) =
            _pager.paginate(position).combine(sendingComments) { paged, sending ->
                PagedElements(
                    elements = sending + paged.elements.map { StatusComment(it, Comment.Status.Empty) },
                    loadState = paged.loadState
                )
            }

        override suspend fun refresh() {
            _pager.refresh()
            sendingComments.update { emptyList() }
        }
    }

    suspend fun send(text: String, before: () -> Unit): Result<Unit> {
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
        before()

        return editor
            .comment(text)
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
            .map()
    }
}