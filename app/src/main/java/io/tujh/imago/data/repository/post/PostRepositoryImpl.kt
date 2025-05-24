package io.tujh.imago.data.repository.post

import android.content.Context
import android.graphics.BitmapFactory
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
    private val textType = "text/plain".toMediaTypeOrNull()

    override suspend fun create(title: String, uris: List<Uri>): Result<Unit> {
        val titleBody = title.toRequestBody(textType)
        val images = uris.mapIndexed { i, uri -> context.formDataOf("file$i", uri) }
        val sizes = uris.joinToString(" ") { uri ->
            val (w, h) = uri.size()
            "$w,$h"
        }
        val sizesBody = sizes.toRequestBody(textType)

        return api.add(titleBody, sizesBody, images)
    }

    override suspend fun checkInFavorite(id: String) =
        api.checkInFavorite(id).map(FavoriteResponse::inFavorite)

    private fun Uri.size(): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val stream = context.contentResolver.openInputStream(this) ?: return -1 to -1
        BitmapFactory.decodeStream(stream, null, options)

        return options.run { outWidth to outHeight }
    }
}