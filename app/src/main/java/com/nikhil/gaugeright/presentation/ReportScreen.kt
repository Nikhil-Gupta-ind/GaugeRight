package com.nikhil.gaugeright.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nikhil.gaugeright.presentation.component.LineChart
import com.nikhil.gaugeright.presentation.component.SpreadsheetView

@Composable
fun ReportScreen(
    state: ScreenState,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier.fillMaxSize()
    ) {
        if (state.readings.isNotEmpty()) {
            LineChart(
                readings = state.readings,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
            SpreadsheetView(state.readings)
        } else {
            EmptyScreen()
        }
    }
}