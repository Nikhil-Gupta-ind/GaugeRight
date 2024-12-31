package com.nikhil.gaugeright.presentation

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.presentation.component.CameraOverlay
import com.nikhil.gaugeright.presentation.component.CameraPreview

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    back: () -> Unit,
    modifier: Modifier = Modifier
) {

    /*var readings by remember {
        mutableStateOf(emptyList<Reading>())
    }*/

    val context = LocalContext.current

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                viewModel.analyzer
            )
        }
    }

    val info by viewModel.analyzer.info.collectAsState()

    LaunchedEffect(info) {
        if (info is CameraState.Processed) {
            viewModel.insertReading((info as CameraState.Processed).reading)
//            readings = listOf((state as CameraState.Processed).reading) // one for now
            back()
        }
    }

    Box(modifier = modifier) {
        CameraPreview(controller, Modifier.fillMaxSize())
        CameraOverlay(
            state = info,
            modifier = Modifier
                .align(Alignment.Center)
        )

        // to see the live reading on cam
        /*Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            readings.forEach {
                Text(
                    text = "${it.value} ${it.timestamp} ${it.isSynced}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }*/
    }
}