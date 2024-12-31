package com.nikhil.gaugeright.presentation

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.nikhil.gaugeright.domain.GaugeReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GaugeImageAnalyzer @Inject constructor(
    private val gaugeReader: GaugeReader
): ImageAnalysis.Analyzer {

    private var _info = MutableStateFlow<CameraState>(CameraState.Idle)
    val info get() = _info.asStateFlow()

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        // At 60 fps analyze 1 frame per sec
        if (frameSkipCounter % 60 == 0) {
            val bitmap = image
                .toBitmap()
            Log.d("Frames", "analyze: $frameSkipCounter")

            CoroutineScope(Dispatchers.Main).launch {
                val res = gaugeReader.getReading(bitmap)
                res.collectLatest {
                    _info.emit(it)
                }
            }
        }
        frameSkipCounter++

        image.close()
    }

}