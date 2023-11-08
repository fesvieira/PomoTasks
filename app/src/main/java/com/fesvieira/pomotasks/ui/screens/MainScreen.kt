package com.fesvieira.pomotasks.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.helpers.formatToString
import com.fesvieira.pomotasks.ui.components.AppFloatActionButton
import com.fesvieira.pomotasks.ui.components.ClockComponent
import com.fesvieira.pomotasks.ui.components.ClockState
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var totalMillis by remember { mutableStateOf(60000) }
    var millis by remember { mutableStateOf(60000) }

    var clockState by remember{ mutableStateOf(ClockState.PAUSED) }

    LaunchedEffect(clockState) {
        val timerJob = async {
            while (millis > 0) {
                delay(50)
                millis -= 50
            }
        }

        when(clockState) {
            ClockState.PAUSED, ClockState.STOPPED -> timerJob.cancel()
            ClockState.PLAYING -> timerJob.start()
        }
    }

    LaunchedEffect(millis) {
        if (millis > 0) return@LaunchedEffect
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
                        clockState = newClockState
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