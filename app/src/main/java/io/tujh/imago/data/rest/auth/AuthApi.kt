package io.tujh.imago.data.rest.auth

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun signIn(@Body body: AuthRequest): Result<AuthResponse>

    @POST("/auth/register")
    suspend fun signUp(@Body body: AuthRequest): Result<AuthResponse>
}