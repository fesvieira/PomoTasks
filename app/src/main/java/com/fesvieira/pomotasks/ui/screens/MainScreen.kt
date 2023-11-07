package com.fesvieira.pomotasks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.helpers.formatToString
import com.fesvieira.pomotasks.ui.components.ClockComponent
import com.fesvieira.pomotasks.ui.components.ClockState
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    var seconds by remember { mutableStateOf(1500) }
    val minutesString by remember(seconds) {
        derivedStateOf {
            val time = (seconds / 60)
            if (time == 0) "" else time.toString()
        }
    }
    val secondsString by remember(seconds) {
        derivedStateOf { (seconds % 60).formatToString }
    }

    var clockState by remember{ mutableStateOf(ClockState.PAUSED) }

    LaunchedEffect(clockState) {
        val timerJob = async {
            while (true) {
                delay(1000)
                seconds -= 1
            }
        }
        when(clockState) {
            ClockState.PAUSED, ClockState.STOPPED -> timerJob.cancel()
            ClockState.PLAYING -> timerJob.start()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ClockComponent(
                    clockState = clockState,
                    minutes = minutesString,
                    seconds = secondsString,
                    onMinutesChange = { minutesString ->
                        seconds = (minutesString.toIntOrNull() ?: 0) * 60
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

@Composable
fun AppFloatActionButton(icon: Painter, onClick: () -> Unit) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            contentDescription = null,
        )
    }
}