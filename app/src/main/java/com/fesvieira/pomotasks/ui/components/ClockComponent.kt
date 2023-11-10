package com.fesvieira.pomotasks.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.helpers.formatToString
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

enum class ClockState {
    PAUSED,
    STOPPED,
    PLAYING
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClockComponent(
    clockState: ClockState,
    seconds: Long,
    totalSeconds: Long,
    onMinutesChange: (String) -> Unit,
    onClockStateChange: (ClockState) -> Unit
) {
    val progressPercentage by animateFloatAsState(
        if (totalSeconds == 0L) {
            1f
        } else {
            seconds.toFloat() / totalSeconds.toFloat()
        },
        label = "progressPercentage",
    )

    val minutes by remember(seconds) {
        derivedStateOf { if (seconds == 0L) "" else (seconds / 60).toString() }
    }

    val secondsString by remember(seconds) {
        derivedStateOf { (seconds % 60).formatToString }
    }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    brush = Brush.radialGradient(
                        with(mtc.onBackground) {
                            listOf(
                                this.copy(alpha = 0.8f),
                                this.copy(alpha = 0.6f),
                                this.copy(alpha = 0.4f),
                                this.copy(alpha = 0.2f),
                                this.copy(alpha = 0.01f),
                            )
                        },
                    ),
                    CircleShape
                )
                .padding(20.dp)
                .background(mtc.tertiaryContainer, CircleShape)
                .padding(16.dp)
                .size(240.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = clockState == ClockState.STOPPED) {
                    TimerPickerComponent(
                        minutes = minutes,
                        onMinutesChanged = onMinutesChange
                    )
                }

                AnimatedVisibility(visible = clockState != ClockState.STOPPED) {
                    Text(
                        text = if (minutes.isNotEmpty()) "$minutes:$secondsString" else secondsString,
                        fontSize = 60.sp,
                        color = mtc.onBackground
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnimatedContent(
                        targetState = if (clockState == ClockState.PLAYING) R.drawable.ic_pause
                        else R.drawable.ic_play, label = "animIcon"
                    ) { id ->
                        Icon(
                            painter = painterResource(id),
                            contentDescription = null,
                            tint = mtc.tertiary.copy(alpha = if (minutes == "") 0.4f else 1.0f),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .then(
                                    if (clockState == ClockState.PLAYING || minutes != "") {
                                        Modifier.clickable {
                                            onClockStateChange(
                                                if (clockState == ClockState.PLAYING) ClockState.PAUSED
                                                else ClockState.PLAYING
                                            )
                                        }
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }

                    AnimatedVisibility(visible = clockState == ClockState.PAUSED) {
                        Icon(
                            painter = painterResource(R.drawable.ic_stop),
                            contentDescription = null,
                            tint = mtc.tertiary,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onClockStateChange(ClockState.STOPPED)
                                }
                        )
                    }
                }
            }
        }

        CircularProgressIndicator(
            progress = progressPercentage,
            color = mtc.tertiary,
            strokeWidth = 10.dp,
            modifier = Modifier
                .align(Alignment.Center)
                .size(240.dp)
        )
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
                seconds = 1,
                totalSeconds = 1,
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