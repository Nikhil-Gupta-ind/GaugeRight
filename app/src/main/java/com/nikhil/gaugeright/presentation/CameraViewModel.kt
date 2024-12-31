package com.nikhil.gaugeright.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.domain.ReadingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: ReadingRepository
): ViewModel(){

    @Inject
    lateinit var analyzer: GaugeImageAnalyzer

    fun insertReading(reading: Reading) {
        viewModelScope.launch {
            repository.insertReading(reading)
        }
    }
}

sealed interface CameraState {
    data object Idle : CameraState
    data object Aligned: CameraState
    data object Processing: CameraState
    data class Processed(val reading: Reading): CameraState
}