package io.tujh.imago.data.repository.user

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.data.dto.UserDto
import io.tujh.imago.data.rest.profile.ProfileApi
import io.tujh.imago.data.retrofit.formDataOf
import io.tujh.imago.data.store.Store
import io.tujh.imago.data.utils.get
import io.tujh.imago.domain.user.ProfileRepository
import io.tujh.imago.domain.utils.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
    private val userStore: Store<UserDto>,
    @ApplicationContext private val context: Context
) : ProfileRepository {
    override suspend fun updateName(name: String): Result<Unit> {
        val current = userStore.get() ?: return Result.failure(NullPointerException())
        return api
            .changeName(name)
            .onSuccess { userStore.put(current.copy(name = name)) }
            .map()
    }

    override suspend fun uploadAvatar(uri: Uri): Result<Unit> {
        val current = userStore.get() ?: return Result.failure(NullPointerException())
        val part = context.formDataOf("file", uri)
        return api
            .upload(part)
            .onSuccess { resp ->
                userStore.put(current.copy(avatar = uri.toString())) // FIXME !!!
            }
            .map()
    }
}