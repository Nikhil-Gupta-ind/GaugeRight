package com.nikhil.gaugeright.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nikhil.gaugeright.R
import com.nikhil.gaugeright.ui.theme.GaugeRightTheme

/**
 * Custom TopBar - Not using now
 */
@Composable
fun TopBar(
    title: String,
    canNavigateUp: Boolean = false,
    onNavigateUp: () -> Unit = {},
    darkTheme: Boolean,
    themeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val rotationAngle by animateFloatAsState(targetValue = if (darkTheme) 180f else 0f)
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            if (canNavigateUp) {
                IconButton(
                    onClick = onNavigateUp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Navigate up",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = themeToggle,
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(
                        id = if (darkTheme) R.drawable.baseline_sunny_24 else R.drawable.moon_clear_fill
                    ),
                    contentDescription = "Change Theme",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .graphicsLayer(rotationZ = rotationAngle),
                )
            }
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopBarPreview() {
    GaugeRightTheme {
        TopBar(
            title = "Readings",
            darkTheme = false,
            themeToggle = {}
        ) {
            Image(
                imageVector = Icons.Default.Star,
                contentDescription = "Sample",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}