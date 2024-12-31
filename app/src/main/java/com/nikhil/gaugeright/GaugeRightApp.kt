package com.nikhil.gaugeright

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.nikhil.gaugeright.workers.ReadingSyncWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GaugeRightApp: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: ReadingSyncWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}