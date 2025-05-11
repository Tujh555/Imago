package io.tujh.imago.data.repository.post

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.data.rest.post.FavoriteResponse
import io.tujh.imago.data.rest.post.PostApi
import io.tujh.imago.data.rest.post.RequestId
import io.tujh.imago.data.retrofit.formDataOf
import io.tujh.imago.domain.post.repository.PostRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: PostApi
) : PostRepository {
    override suspend fun create(title: String, uris: List<Uri>): Result<Unit> {
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val images = uris.mapIndexed { i, uri -> context.formDataOf("file$i", uri) }

        return api.add(titleBody, images)
    }

    override suspend fun checkInFavorite(id: String) =
        api.checkInFavorite(RequestId(id)).map(FavoriteResponse::inFavorite)
}