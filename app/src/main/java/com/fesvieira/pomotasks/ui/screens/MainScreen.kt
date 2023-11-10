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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.PomodoroViewModel
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.helpers.isAllowedTo
import com.fesvieira.pomotasks.ui.components.AppFloatActionButton
import com.fesvieira.pomotasks.ui.components.ClockComponent
import com.fesvieira.pomotasks.ui.components.ClockState
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    pomodoroViewModel: PomodoroViewModel
) {
    val context = LocalContext.current
    val clockState by pomodoroViewModel.clockState.collectAsState()
    val totalSeconds by pomodoroViewModel.totalSeconds.collectAsState()
    val seconds by pomodoroViewModel.seconds.collectAsState()

    val permissionsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { allowed ->
            if (allowed) {
                pomodoroViewModel.setClockState(ClockState.PLAYING)
            } else {
                Toast.makeText(
                    context,
                    "We need permission to play Alarms",
                    Toast.LENGTH_LONG
                ).show()
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
                        val newValue = (minutesString.toLongOrNull() ?: 0L) * 60L
                        pomodoroViewModel.setTotalSeconds(newValue)
                        pomodoroViewModel.setSeconds(newValue)
                    },
                    onClockStateChange = { newClockState ->
                        if (
                            newClockState == ClockState.PLAYING &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ) {
                            if (context.isAllowedTo(POST_NOTIFICATIONS)) {
                                pomodoroViewModel.setClockState(newClockState)
                            } else {
                                permissionsLauncher.launch(POST_NOTIFICATIONS)
                            }
                        } else {
                            pomodoroViewModel.setClockState(newClockState)
                        }
                    }
                )
            }
        }
    }
}