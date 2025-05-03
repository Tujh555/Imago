package io.tujh.imago.work

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.tujh.imago.domain.post.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PostUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: PostRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val (title, uris) = inputData.parse()
        val res = withContext(Dispatchers.IO) { repository.create(title, uris) }

        return if (res.isSuccess) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    companion object {
        private const val TITLE = "title"
        private const val URIS = "uris"

        operator fun invoke(context: Context, title: String, uris: List<Uri>) =
            OneTimeWorkRequestBuilder<PostUploadWorker>()
                .setInputData(buildData(title, uris))
                .build()
                .let(WorkManager.getInstance(context)::enqueue)

        private fun buildData(title: String, uris: List<Uri>) = workDataOf(
            TITLE to title,
            URIS to uris.map { it.toString() }.toTypedArray()
        )

        private fun Data.parse() =
            getString(TITLE).orEmpty() to getStringArray(URIS).orEmpty().map(Uri::parse)
    }
}