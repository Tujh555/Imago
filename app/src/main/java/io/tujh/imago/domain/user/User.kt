package io.tujh.imago.domain.user

data class User(
    val id: String,
    val avatar: String?,
    val name: String,
    val email: String
)