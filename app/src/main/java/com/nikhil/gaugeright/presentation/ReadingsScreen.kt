package com.nikhil.gaugeright.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nikhil.gaugeright.R
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.ui.theme.GaugeRightTheme
import java.sql.Timestamp
import com.nikhil.gaugeright.util.formatTimestamp

@Composable
fun ReadingsScreen(
    state: ScreenState,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier.fillMaxSize()) {
        if (state.latestReading.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.latestReading.size) {
                    ReadingItem(reading = state.latestReading[it])
                }
                item {
                    Spacer(modifier = Modifier.fillMaxSize().height(100.dp))
                }
            }
        } else {
            EmptyScreen()
        }
    }
}

@Composable
fun EmptyScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            painter = painterResource(R.drawable.speed_meter),
            contentDescription = "Sample",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Tap on camera to capture reading.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReadingItem(
    reading: Reading,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.speed_meter),
                contentDescription = "Sample",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Reading: ${reading.value} unit",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = reading.timestamp.formatTimestamp(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                painter = painterResource(if (reading.isSynced) R.drawable.baseline_cloud_done_24 else R.drawable.baseline_cloud_off_24),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = if (reading.isSynced) "Synced" else "Not Synced",
            )
        }
    }
}



@Preview
@Composable
private fun ReadingItemPreview() {
    GaugeRightTheme {
        ReadingItem(
            Reading(
                value = 84,
                timestamp = Timestamp(System.currentTimeMillis()),
                isSynced = false
            )
        )
    }
}