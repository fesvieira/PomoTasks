package com.fesvieira.pomotasks.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme as mtc
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme

enum class ClockState {
    PAUSED,
    STOPPED,
    PLAYING
}

@Composable
fun ClockComponent(
    clockState: ClockState,
    minutes: String,
    seconds: String,
    onMinutesChange: (String) -> Unit,
    onClockStateChange: (ClockState) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(
                brush = Brush.radialGradient(
                    with(mtc.onBackground) {
                        if (clockState == ClockState.PLAYING) {
                            listOf(
                                this.copy(alpha = 1.0f),
                                this.copy(alpha = 0.8f),
                                this.copy(alpha = 0.5f),
                                this.copy(alpha = 0.3f),
                                this.copy(alpha = 0.01f),
                            )
                        } else {
                            listOf(this.copy(alpha = 0.0f), this.copy(alpha = 0.0f))
                        }

                    },
                ),
                CircleShape
            )
            .padding(20.dp)
            .background(mtc.tertiaryContainer, CircleShape)
            .padding(16.dp)
            .size(240.dp)
            .border(10.dp, mtc.tertiary, CircleShape)

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(visible = clockState == ClockState.STOPPED) {
                TimerPickerComponent(minutes = minutes, onMinutesChanged = onMinutesChange)
            }

            AnimatedVisibility(visible = clockState != ClockState.STOPPED) {
                Text(
                    text = if(minutes.isNotEmpty()) "$minutes:$seconds" else seconds,
                    fontSize = 60.sp,
                    color = mtc.onBackground
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(
                    painter = painterResource(
                        if (clockState == ClockState.PLAYING) R.drawable.ic_pause
                        else R.drawable.ic_play
                    ),
                    contentDescription = null,
                    tint = mtc.tertiary.copy(alpha = if (minutes == "") 0.4f else 1.0f),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            if (clockState == ClockState.PLAYING || minutes != "") {
                                onClockStateChange(
                                    if (clockState == ClockState.PLAYING) ClockState.PAUSED
                                    else ClockState.PLAYING
                                )
                            }
                        }
                )

                if (clockState == ClockState.PAUSED) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stop),
                        contentDescription = null,
                        tint = mtc.tertiary,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                onClockStateChange(ClockState.STOPPED)
                            }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewClockComponent() {
    val view: @Composable () -> Unit = {
        Box(
            Modifier
                .background(mtc.background)
                .padding(16.dp)
        ) {
            ClockComponent(
                clockState = ClockState.PLAYING,
                minutes = "23",
                seconds = "00",
                onMinutesChange = { },
                onClockStateChange = { }
            )
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PomoTasksTheme {
            view()
        }
        PomoTasksTheme(darkTheme = true) {
            view()
        }
    }

}