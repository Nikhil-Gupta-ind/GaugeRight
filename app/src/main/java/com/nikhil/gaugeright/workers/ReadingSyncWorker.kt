package com.nikhil.gaugeright.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nikhil.gaugeright.R
import com.nikhil.gaugeright.data.local.ReadingDao
import com.nikhil.gaugeright.data.remote.ApiService
import com.nikhil.gaugeright.data.remote.Resource
import com.nikhil.gaugeright.data.remote.SafeApi
import com.nikhil.gaugeright.util.CHANNEL_ID
import com.nikhil.gaugeright.util.KEY_OUTPUT_URI
import com.nikhil.gaugeright.util.NOTIFICATION_ID
import com.nikhil.gaugeright.util.NOTIFICATION_TITLE
import com.nikhil.gaugeright.util.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
import com.nikhil.gaugeright.util.VERBOSE_NOTIFICATION_CHANNEL_NAME
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class ReadingSyncWorker @AssistedInject constructor(
    private val readingDao: ReadingDao,
    private val apiService: ApiService,
    @Assisted context: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            setForeground(getForegroundInfo(applicationContext))
        } catch (e: Exception) {
            return Result.failure()
        }

        delay(3000) // simulating
        val readings = readingDao.getUnsyncedData()
        val res = SafeApi.call { apiService.uploadReadings() }
        Log.d("SyncWork", "readings: $readings\nres: $res")
        when (res) {
            is Resource.Success -> {
                readingDao.updateSyncStatus(
                    readings.map { it.id }
                )
                return Result.success(workDataOf(KEY_OUTPUT_URI to "done"))
            }
            is Resource.Error -> return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(applicationContext)
    }
}

private fun getForegroundInfo(context: Context): ForegroundInfo {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ForegroundInfo(
            NOTIFICATION_ID,
            createNotification("sync...", context),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    } else {
        ForegroundInfo(
            NOTIFICATION_ID,
            createNotification("sync...", context)
        )
    }
}

fun createNotification(message: String, context: Context): Notification {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Show the notification
//    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())

    return builder.build()
}