package io.tujh.imago.presentation.models

import androidx.compose.runtime.Immutable
import io.tujh.imago.domain.user.User

@Immutable
data class UserItem(
    val id: String,
    val avatar: String?,
    val name: String,
    val email: String
)

fun User.toUi() = UserItem(id, avatar, name, email)