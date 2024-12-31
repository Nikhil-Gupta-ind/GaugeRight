package com.nikhil.gaugeright.domain

import android.graphics.Bitmap
import com.nikhil.gaugeright.presentation.CameraState
import kotlinx.coroutines.flow.Flow

interface GaugeReader {

    suspend fun getReading(bitmap: Bitmap): Flow<CameraState>
}