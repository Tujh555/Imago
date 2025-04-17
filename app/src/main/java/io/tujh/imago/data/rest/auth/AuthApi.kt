package io.tujh.imago.data.rest.auth

import io.tujh.imago.data.rest.auth.AuthRequest
import io.tujh.imago.data.rest.auth.AuthResponse
import io.tujh.imago.data.rest.auth.LogoutRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun signIn(@Body body: AuthRequest): Result<AuthResponse>

    @POST("/auth/register")
    suspend fun signUp(@Body body: AuthRequest): Result<AuthResponse>

    @POST("/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Result<Unit>
}