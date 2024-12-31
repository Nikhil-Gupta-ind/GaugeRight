package com.nikhil.gaugeright.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.util.formatTimestamp

@Composable
fun SpreadsheetView(
    readings: List<Reading>,
    modifier: Modifier = Modifier
) {
    val header = listOf("S.no.", "Value", "Timestamp", "Synced")
    Column(
        modifier = modifier
    ) {
        TableHeader(header)
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
            items(readings) { reading ->
                TableRow(reading)
                Spacer(
                    Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun TableHeader(header: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RectangleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            header.forEachIndexed { index, column ->
                Text(
                    text = column,
                    modifier = if (index > 0) Modifier.weight(1f) else Modifier.weight(0.5f),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TableRow(reading: Reading) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        Text(
            text = "${reading.id}",
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
        VerticalLine()
        Text(
            text = "${reading.value}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
        VerticalLine()
        Text(
            text = reading.timestamp.formatTimestamp(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
        VerticalLine()
        Text(
            text = if (reading.isSynced) "Online" else "Local",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}

@Composable
fun VerticalLine(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .width(1.dp)
            .height(48.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    )
}