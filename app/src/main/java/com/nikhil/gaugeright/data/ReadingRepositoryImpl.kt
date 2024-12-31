package com.nikhil.gaugeright.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.nikhil.gaugeright.data.local.ReadingDao
import com.nikhil.gaugeright.data.remote.ApiService
import com.nikhil.gaugeright.data.remote.Resource
import com.nikhil.gaugeright.data.remote.SafeApi
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.domain.ReadingRepository
import com.nikhil.gaugeright.workers.ReadingSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import java.time.Duration
import javax.inject.Inject

class ReadingRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val readingDao: ReadingDao,
    private val apiService: ApiService
): ReadingRepository {

    private var lastWorkId: String? = null
    private val TAG_OUTPUT = "OUTPUT"
    private val workManager = WorkManager.getInstance(context)

    override val syncInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagFlow(TAG_OUTPUT).mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }.filter {
            val bool = it.id.toString() == lastWorkId
            Log.d("WorkInfo", "filter : ${it.id} ${it.state} $bool")
            bool
        }

    override fun getReadings() = readingDao.getAllReadings()

    override fun getLatestReadings() = readingDao.getLatestReadings()

    override suspend fun insertReading(reading: Reading) = readingDao.upsertReadings(listOf(reading))

    override suspend fun upload(readings: List<Reading>): Flow<Resource<Any>> = flow {
        delay(3000) // simulating
        val res = SafeApi.call { apiService.uploadReadings() }

        // just for this fake api checking this otherwise we can just emit(res)
        when (res) {
            is Resource.Error -> emit(Resource.Error(res.message))
            is Resource.Success -> {
                readingDao.upsertReadings(
                    readings
                        .filter { !it.isSynced }
                        .map { it.copy(isSynced = true) }
                )
                emit(Resource.Success("Success Data Object"))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun syncNow(readings: List<Reading>) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<ReadingSyncWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(15)
            )
            .addTag(TAG_OUTPUT)
//            .setConstraints(constraints)
            .build()
        lastWorkId = workRequest.id.toString()
        Log.d("WorkInfo", "request: $lastWorkId")
        workManager.enqueueUniqueWork(
            "unique_work_name",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}