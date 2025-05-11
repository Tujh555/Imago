package io.tujh.imago.presentation.models

import androidx.compose.runtime.Immutable
import io.tujh.imago.domain.post.model.Comment
import io.tujh.imago.domain.post.model.StatusComment
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter by lazy {
    val pattern = "dd.MM.yy HH:mm"
    DateTimeFormatter
        .ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
}

@Immutable
data class CommentItem(
    val id: String,
    val author: UserItem,
    val createdAt: String,
    val text: String,
    val status: Comment.Status
)

fun StatusComment.toUi() = CommentItem(
    id = comment.id,
    author = comment.author.toUi(),
    createdAt = timeFormatter.format(comment.createdAt),
    text = comment.text,
    status = status
)