package com.nikhil.gaugeright.presentation.component

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.nikhil.gaugeright.domain.Reading
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.DefaultColors
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun LineChart(
    readings: List<Reading>,
    modifier: Modifier = Modifier
) {

    var xPos by remember { mutableFloatStateOf(0f) }
    val modelProducer = remember { ChartEntryModelProducer() }
    val dataPoints = remember { arrayListOf<FloatEntry>() }
    val datasetForModel = remember { mutableStateListOf(listOf<FloatEntry>()) }
    val datasetLineSpec = remember { arrayListOf<LineChart.LineSpec>() }
    val scrollState = rememberChartScrollState()

    val color = MaterialTheme.colorScheme.primary
    LaunchedEffect(key1 = Unit) {
        datasetForModel.clear()
        datasetLineSpec.clear()
        datasetLineSpec.add(
            LineChart.LineSpec(
                lineColor = color.toArgb(),
                lineBackgroundShader = DynamicShaders.fromBrush(
                    brush = Brush.verticalGradient(
                        listOf(
                            color,
                            color.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                        )
                    )
                )
            )
        )
        readings.forEach {
            dataPoints.add(
                FloatEntry(
                    x = xPos,
                    y = (it.value.toDouble().toInt()).toFloat()
                )
            )
            xPos += 1f
        }
        datasetForModel.add(dataPoints)
        modelProducer.setEntries(datasetForModel)
        scrollState.animateScrollBy(scrollState.maxValue)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        if (datasetForModel.isNotEmpty()) {
            val defaultColors = remember {
                DefaultColors.Light
            }
            ProvideChartStyle(
                chartStyle = ChartStyle.fromColors(
                    axisGuidelineColor = MaterialTheme.colorScheme.primary,
                    axisLabelColor = MaterialTheme.colorScheme.primary,
                    axisLineColor = Color.Transparent,
                    entityColors = listOf(
                        defaultColors.entity1Color,
                        defaultColors.entity2Color,
                        defaultColors.entity3Color,
                    ).map(::Color),
                    elevationOverlayColor = Color(defaultColors.elevationOverlayColor)
                )
            ) {
                Chart(
                    chart = lineChart(
                        lines = datasetLineSpec
                    ),
                    chartModelProducer = modelProducer,
                    startAxis = rememberStartAxis(
                        title = "Value",
                        tick = null,
                        valueFormatter = { value, _ ->
                            value.toInt().toString()
                        },
                        itemPlacer = AxisItemPlacer.Vertical.default(
                            maxItemCount = 5
                        )
                    ),
                    chartScrollState = scrollState,
                    isZoomEnabled = true,
                )
            }
        }
    }
}