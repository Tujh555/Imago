package io.tujh.imago.data.repository.auth

import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.rest.auth.AuthApi
import io.tujh.imago.data.rest.auth.AuthRequest
import io.tujh.imago.data.rest.auth.AuthResponse
import io.tujh.imago.data.rest.auth.LogoutRequest
import io.tujh.imago.data.store.Store
import io.tujh.imago.domain.auth.AuthData
import io.tujh.imago.domain.auth.AuthRepository
import io.tujh.imago.domain.utils.map
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: Store<String>,
    private val userStore: Store<UserDto>
) : AuthRepository {
    override suspend fun signIn(data: AuthData) = request(data, api::signIn)

    override suspend fun signUp(data: AuthData) = request(data, api::signUp)

    override suspend fun logout(): Result<Unit> {
        val request = userStore.data.filterNotNull().first().id.let(::LogoutRequest)
        return api.logout(request).onSuccess {
            tokenStore.clear()
            userStore.clear()
        }
    }

    private suspend fun request(
        data: AuthData,
        factory: suspend (AuthRequest) -> Result<AuthResponse>
    ): Result<Unit> {
        val request = AuthRequest(data.email, data.password, data.name)
        val result = factory(request).onSuccess { response ->
            tokenStore.put(response.token)
            userStore.put(response.user)
        }

        return result.map()
    }
}