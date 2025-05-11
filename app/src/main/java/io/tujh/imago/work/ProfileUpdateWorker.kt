package io.tujh.imago.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.tujh.imago.R
import io.tujh.imago.domain.user.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ProfileUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ProfileRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uri = inputData.parse()?.toUri() ?: return Result.failure()

        setForeground(buildInfo())

        val res = withContext(Dispatchers.IO) {
            repository.uploadAvatar(uri)
        }

        return if (res.isSuccess) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun buildInfo(): ForegroundInfo {
        val title = "Uploading photo"
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
        private const val URI = "uri"

        fun start(context: Context, uri: Uri): Operation {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return OneTimeWorkRequestBuilder<ProfileUpdateWorker>()
                .setConstraints(constraints)
                .setInputData(buildData(uri))
                .build()
                .let(WorkManager.getInstance(context)::enqueue)
        }

        private fun buildData(uri: Uri) = workDataOf(
            URI to uri.toString()
        )

        private fun Data.parse() = getString(URI)
    }
}