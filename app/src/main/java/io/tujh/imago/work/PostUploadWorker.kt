package io.tujh.imago.work

import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.tujh.imago.R
import io.tujh.imago.domain.post.repository.PostRepository
import io.tujh.imago.domain.utils.withMinDelay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@HiltWorker
class PostUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: PostRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val (title, uris) = inputData.parse()

        setForeground(buildInfo())

        val res = withContext(Dispatchers.IO) {
            withMinDelay(1500) { repository.create(title, uris) }
        }

        return if (res.isSuccess) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun buildInfo(): ForegroundInfo {
        val title = "Uploading photos..."
        val cancelIntent = WorkManager
            .getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_atom_foreground)
            .setContentTitle(title)
            .setTicker(title)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_close, "Cancel", cancelIntent)
            .build()

        return ForegroundInfo(
            notificationId(),
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    companion object {
        private const val TITLE = "title"
        private const val URIS = "uris"

        fun start(context: Context, title: String, uris: List<Uri>): Flow<WorkInfo?> {
            val request = OneTimeWorkRequestBuilder<PostUploadWorker>()
                .setInputData(buildData(title, uris))
                .build()

            val wm = WorkManager.getInstance(context)
            wm.enqueue(request)

            return wm.getWorkInfoByIdFlow(request.id)
        }

        private fun buildData(title: String, uris: List<Uri>) = workDataOf(
            TITLE to title,
            URIS to uris.map { it.toString() }.toTypedArray()
        )

        private fun Data.parse() =
            getString(TITLE).orEmpty() to getStringArray(URIS).orEmpty().map(Uri::parse)
    }
}