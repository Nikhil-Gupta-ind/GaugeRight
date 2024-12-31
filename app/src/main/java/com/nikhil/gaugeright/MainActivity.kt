package com.nikhil.gaugeright

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.nikhil.gaugeright.presentation.CameraScreen
import com.nikhil.gaugeright.presentation.CameraState
import com.nikhil.gaugeright.presentation.GaugeImageAnalyzer
import com.nikhil.gaugeright.presentation.ReportScreen
import com.nikhil.gaugeright.presentation.ReadingsScreen
import com.nikhil.gaugeright.presentation.ReadingsViewModel
import com.nikhil.gaugeright.presentation.ScreenState
import com.nikhil.gaugeright.presentation.SyncState
import com.nikhil.gaugeright.ui.theme.GaugeRightTheme
import com.nikhil.gaugeright.workers.ReadingSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        enableEdgeToEdge()
        setContent {
            val viewModel: ReadingsViewModel = hiltViewModel()
            val screenState by viewModel.state.collectAsState()

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                viewModel.uiEvent.collectLatest { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            GaugeRightTheme(darkTheme = screenState.darkTheme) {
                GaugeRight(
                    screenState = screenState,
                    sync = viewModel::syncNow,
                    themeToggle = viewModel::toggleDarkMode
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun syncNow() {
        val workRequest = OneTimeWorkRequestBuilder<ReadingSyncWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(15)
            )
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaugeRight(
    screenState: ScreenState,
    sync: () -> Unit,
    themeToggle: () -> Unit = {}
) {
    val navController = rememberNavController()

    var route by remember { mutableStateOf("") }
    LaunchedEffect(navController.currentBackStackEntryFlow) {
        navController.currentBackStackEntryFlow.collect {
            route = it.destination.route ?: ""
        }
    }

    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            navController.navigate(Screens.CAMERA.name)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            if (route != Screens.CAMERA.name) {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            text = route,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (route == Screens.REPORT.name) {
                            IconButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Navigate up",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        val rotationAngle by animateFloatAsState(targetValue = if (screenState.darkTheme) 180f else 0f)
                        IconButton(
                            onClick = themeToggle,
                            modifier = Modifier
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (screenState.darkTheme) R.drawable.baseline_sunny_24 else R.drawable.moon_clear_fill
                                ),
                                contentDescription = "Change Theme",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.graphicsLayer(rotationZ = rotationAngle),
                            )
                        }

                        when(route) {
                            Screens.READINGS.name -> {
                                IconButton(
                                    onClick = { navController.navigate(Screens.REPORT.name) }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.bar_chart),
                                        contentDescription = "Sample",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Screens.REPORT.name -> {
                                var angle by remember { mutableFloatStateOf(0f) }
                                val animatedRotation by animateFloatAsState(
                                    targetValue = angle,
                                    animationSpec = tween(durationMillis = 1000),
                                    label = "rotation"
                                )
                                LaunchedEffect(Unit) {
                                    while (true) {
                                        angle += 360f
                                        delay(1000)
                                    }
                                }
                                IconButton(onClick = sync) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_sync_24),
                                        contentDescription = "Sync Now",
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .rotate(if (screenState.syncState == SyncState.SYNCING) animatedRotation else 0f)
                                            .scale(scaleX = -1f, scaleY = 1f)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (route != Screens.CAMERA.name) {
                FloatingActionButton(
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                cameraPermission
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                navController.navigate(Screens.CAMERA.name)
                            }
                            else -> {
                                launcher.launch(cameraPermission)
                            }
                        }

                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_camera_24),
                        contentDescription = "Camera",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.READINGS.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.CAMERA.name) {
                CameraScreen(
                    back = { navController.popBackStack() }
                )
            }
            composable(Screens.READINGS.name) {
                ReadingsScreen(
                    state = screenState
                )
            }
            composable(Screens.REPORT.name) {
                ReportScreen(
                    state = screenState
                )
            }
        }
    }
}

enum class Screens {
    CAMERA, READINGS, REPORT
}