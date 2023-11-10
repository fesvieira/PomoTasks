package com.fesvieira.pomotasks.ui.screens

import android.Manifest.permission.POST_NOTIFICATIONS
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
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.helpers.isAllowedTo
import com.fesvieira.pomotasks.ui.components.AppFloatActionButton
import com.fesvieira.pomotasks.ui.components.ClockComponent
import com.fesvieira.pomotasks.ui.components.ClockState
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import com.fesvieira.pomotasks.workers.NotificationService
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var totalSeconds by remember { mutableStateOf(25 * 60L) }
    var seconds by remember { mutableStateOf(25 * 60L) }
    var clockState by remember { mutableStateOf(ClockState.STOPPED) }
    val context = LocalContext.current
    var alarmTime by remember { mutableStateOf<LocalDateTime?>(null)}

    val permissionsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { allowed ->
            if (allowed) {
                clockState = ClockState.PLAYING
            } else {
                Toast.makeText(
                    context,
                    "We need permission to play Alarms",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    LaunchedEffect(clockState) {
        val timerJob = async {
            alarmTime = LocalDateTime.now().plusSeconds(seconds + 1)
            while (seconds > 0) {
                delay(1000)
                seconds = Duration.between(LocalDateTime.now(), alarmTime).seconds
                if (seconds <= 0L) {
                    NotificationService.scheduleNotification(context)
                }
            }
        }

        when (clockState) {
            ClockState.PAUSED, ClockState.STOPPED -> timerJob.cancel()
            ClockState.PLAYING -> timerJob.start()
        }
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
                    seconds = seconds,
                    totalSeconds = totalSeconds,
                    onMinutesChange = { minutesString ->
                        totalSeconds = (minutesString.toLongOrNull() ?: 0L) * 60L
                        seconds = totalSeconds
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