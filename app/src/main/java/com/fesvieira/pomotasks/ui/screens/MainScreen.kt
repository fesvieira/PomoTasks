package com.fesvieira.pomotasks.ui.screens

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.WorkerParameters
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.helpers.isAllowedTo
import com.fesvieira.pomotasks.ui.components.AppFloatActionButton
import com.fesvieira.pomotasks.ui.components.ClockComponent
import com.fesvieira.pomotasks.ui.components.ClockState
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import com.fesvieira.pomotasks.workers.NotificationService
import com.fesvieira.pomotasks.workers.NotificationWorker
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var totalMillis by remember { mutableStateOf(25 * 60 * 1000) }
    var millis by remember { mutableStateOf(25 * 60 * 1000) }
    var clockState by remember { mutableStateOf(ClockState.STOPPED) }
    val context = LocalContext.current

    val permissionsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { allowed ->
            if (allowed) {
                clockState = ClockState.PLAYING
            } else {
                Toast.makeText(
                    context,
                    "We need notification permission to play Pomodoro Alarms",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    LaunchedEffect(clockState) {
        val timerJob = async {
            while (millis > 0) {
                delay(50)
                millis -= 50
                if (millis == 0) {
                    NotificationService.scheduleNotification(context)
                }
            }
        }

        when (clockState) {
            ClockState.PAUSED, ClockState.STOPPED -> timerJob.cancel()
            ClockState.PLAYING -> timerJob.start()
        }
    }

    LaunchedEffect(millis) {
        if (millis > 0 && clockState == ClockState.PLAYING) return@LaunchedEffect
        millis = totalMillis
        clockState = ClockState.STOPPED
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = mtc.background
    ) {
        Scaffold(
            floatingActionButton = {
                AppFloatActionButton(icon = painterResource(R.drawable.ic_add)) {

                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(mtc.background)
            ) {
                ClockComponent(
                    clockState = clockState,
                    millis = millis,
                    totalMillis = totalMillis,
                    onMinutesChange = { minutesString ->
                        totalMillis = (minutesString.toIntOrNull() ?: 0) * 60000
                        millis = totalMillis
                    },
                    onClockStateChange = { newClockState ->
                        if (
                            newClockState == ClockState.PLAYING &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ) {
                            if (context.isAllowedTo(POST_NOTIFICATIONS)) {
                                clockState = newClockState
                            } else {
                                permissionsLauncher.launch(POST_NOTIFICATIONS)
                            }
                        } else {
                            clockState = newClockState
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    PomoTasksTheme {
        MainScreen()
    }
}