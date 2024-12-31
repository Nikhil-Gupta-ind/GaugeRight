package com.nikhil.gaugeright.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.nikhil.gaugeright.data.local.ReadingDao
import com.nikhil.gaugeright.data.remote.ApiService
import javax.inject.Inject

/**
 * To inject custom dependency
 */
class ReadingSyncWorkerFactory @Inject constructor(
    private val readingDao: ReadingDao,
    private val apiService: ApiService
): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = ReadingSyncWorker(readingDao, apiService, appContext, workerParameters)
}