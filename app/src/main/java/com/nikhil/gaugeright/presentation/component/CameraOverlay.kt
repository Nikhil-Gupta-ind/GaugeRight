package com.nikhil.gaugeright.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nikhil.gaugeright.presentation.CameraState

@Composable
fun CameraOverlay(
    state: CameraState, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(300.dp)
            .rotate(if (state == CameraState.Idle) 10f else 0f)
            .border(
                4.dp, when (state) {
                    CameraState.Idle -> {
                        Color.Red
                    }

                    CameraState.Aligned -> {
                        Color.Blue
                    }

                    CameraState.Processing -> {
                        Color.Cyan
                    }

                    is CameraState.Processed -> {
                        Color.Green
                    }
                }
            )

    ) {
        if (state == CameraState.Processing) {
            AnimatedLine(300.dp)
        }
    }
}