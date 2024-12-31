package com.nikhil.gaugeright.data.ai

import android.graphics.Bitmap
import com.nikhil.gaugeright.domain.GaugeReader
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.presentation.CameraState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.sql.Timestamp
import javax.inject.Inject

/**
 * Actual Pre-Trained AI Model of GaugeReader is required
 * For this prototype using this class to simulate the behaviour
 */
class SimulatedGaugeReader @Inject constructor(): GaugeReader {

    override suspend fun getReading(bitmap: Bitmap) = flow {
//        emit(CameraState.Idle)
        delay(1000)
        emit(CameraState.Aligned)
        delay(500)
        emit(CameraState.Processing)
        delay(1000)
        emit(
            CameraState.Processed(
                Reading(
                    value = (1..100).random(),
                    timestamp = Timestamp(System.currentTimeMillis()),
                    isSynced = false
                )
            )
        )
    }
}