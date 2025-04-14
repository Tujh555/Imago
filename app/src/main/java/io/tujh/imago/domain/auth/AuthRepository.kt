package io.tujh.imago.domain.auth

interface AuthRepository {
    suspend fun signIn(data: AuthData): Result<Unit>

    suspend fun signUp(data: AuthData): Result<Unit>

    suspend fun logout(): Result<Unit>
}

class AuthData(
    val email: String,
    val password: String,
    val name: String
)