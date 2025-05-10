package io.tujh.imago.presentation.models

import androidx.compose.runtime.Immutable
import io.tujh.imago.domain.post.model.Comment
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
)

fun Comment.toUi() = CommentItem(
    id = id,
    author = author.toUi(),
    createdAt = timeFormatter.format(createdAt),
    text = text,
)