package com.nikhil.gaugeright.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.nikhil.gaugeright.data.remote.Resource
import com.nikhil.gaugeright.domain.GaugeReader
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.domain.ReadingRepository
import com.nikhil.gaugeright.util.KEY_OUTPUT_URI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingsViewModel @Inject constructor(
    private val repository: ReadingRepository
): ViewModel(){

    // one time event like toast or snack bar
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent get() = _uiEvent.asSharedFlow()

    // screen state holder
    private val _state = MutableStateFlow(ScreenState())
    val state get() = _state.asStateFlow()
    /*val state: StateFlow<ScreenState> = repository.syncInfo
//        .map { info ->
            when {
                info.state.isFinished -> {
                    _state.value.copy(syncState = SyncState.IDLE)
                }
                info.state == WorkInfo.State.CANCELLED -> {
//                    _state.update {
//                        it.copy(syncState = SyncState.IDLE)
//                    }
                    _state.value.copy(syncState = SyncState.IDLE)
                }
                else -> {
                    _state.value.copy(syncState = SyncState.SYNCING)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScreenState()
        )*/

    /*val state =
        combine(
            _state,
            repository.syncInfo
        ) {
            state, info ->
            when {
                info.state.isFinished -> {
                    state.copy(syncState = SyncState.IDLE)
                }
                info.state == WorkInfo.State.CANCELLED -> {
                    state.copy(syncState = SyncState.IDLE)
                }
                else -> state.copy(syncState = SyncState.SYNCING)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScreenState()
        )*/

    init {
        getReadings() // for report screen
        getLatestReadings() // for reading screen order by desc
        updateSyncInfoInState()
    }

    private fun getReadings() {
        viewModelScope.launch {
            repository.getReadings().collect { readings ->
                _state.update {
                    it.copy(readings = readings)
                }
            }
        }
    }

    private fun getLatestReadings() {
        viewModelScope.launch {
            repository.getLatestReadings().collect { readings ->
                _state.update {
                    it.copy(latestReading = readings)
                }
            }
        }
    }

    private fun updateSyncInfoInState() {
        viewModelScope.launch {
            repository.syncInfo?.collectLatest { info ->
                if (info == null) return@collectLatest
                _state.update {
                    it.copy(syncState = SyncState.IDLE)
                }
                when {
//                    info.state.isFinished -> {
//                        _uiEvent.emit("Sync Success!")
//                    }
                    info.state == WorkInfo.State.SUCCEEDED -> {
                        _uiEvent.emit("Sync Success!")
                    }
                    info.state == WorkInfo.State.FAILED -> {
                        _uiEvent.emit("Sync Failed!")
                    }
                    info.state == WorkInfo.State.RUNNING -> {
                        _state.update {
                            it.copy(syncState = SyncState.SYNCING)
                        }
                    }
                }
            }
        }
    }

    fun toggleDarkMode() {
        _state.update {
            it.copy(darkTheme = !it.darkTheme)
        }
    }

    fun syncNow() { // workManager
        if (state.value.readings.isNotEmpty()) {
            repository.syncNow(state.value.readings)
        }
    }

    fun sync() { // Simple Upload
        if (state.value.readings.isNotEmpty()) {
            _state.update {
                it.copy(syncState = SyncState.SYNCING)
            }
            viewModelScope.launch {
                repository.upload(state.value.readings).collect { res ->
                    _state.update {
                        it.copy(syncState = SyncState.IDLE)
                    }
                    when (res) {
                        is Resource.Error -> {
                            _uiEvent.emit("Error: ${res.message}")
                        }
                        is Resource.Success -> {
                            _uiEvent.emit("Sync Success!")
                        }
                    }
                }
            }
        }
    }
}

data class ScreenState(
    val readings: List<Reading> = emptyList(),
    val latestReading: List<Reading> = emptyList(),
    val syncState: SyncState = SyncState.IDLE,
    var darkTheme: Boolean = false
)

enum class SyncState {
    IDLE, SYNCING
}
