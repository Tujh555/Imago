package io.tujh.imago.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.R
import io.tujh.imago.domain.post.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class PostUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: PostRepository
) : CoroutineWorker(context, params) {
    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override suspend fun doWork(): Result {
        val (title, uris) = inputData.parse()
        setForeground(buildInfo())
        val res = withContext(Dispatchers.IO) {
            delay(100000)
            repository.create(title, uris)
        }

        return if (res.isSuccess) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun buildInfo(): ForegroundInfo {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Uploader",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        val title = "Uploading photos..."
        val cancelIntent = WorkManager
            .getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_atom_foreground) // TODO check
            .setContentTitle(title)
            .setTicker(title)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_close, "Cancel", cancelIntent)
            .build()

        return ForegroundInfo(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    companion object {
        private const val CHANNEL_ID = "my_foreground_channel"
        private const val NOTIFICATION_ID = 1
        private const val TITLE = "title"
        private const val URIS = "uris"

        fun start(context: Context, title: String, uris: List<Uri>) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            OneTimeWorkRequestBuilder<PostUploadWorker>()
                .setConstraints(constraints)
                .setInputData(buildData(title, uris))
                .build()
                .let(WorkManager.getInstance(context)::enqueue)
        }

        private fun buildData(title: String, uris: List<Uri>) = workDataOf(
            TITLE to title,
            URIS to uris.map { it.toString() }.toTypedArray()
        )

        private fun Data.parse() =
            getString(TITLE).orEmpty() to getStringArray(URIS).orEmpty().map(Uri::parse)
    }
}