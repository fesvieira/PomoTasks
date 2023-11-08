package com.fesvieira.pomotasks.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import androidx.compose.material3.MaterialTheme.colorScheme as ThemeColor

@Composable
fun TimerPickerComponent(
    minutes: String,
    onMinutesChanged: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(ThemeColor.tertiaryContainer)
    ) {
        TimerPickerTextField(
            time = minutes,
            onTimeChanged = {
                onMinutesChanged(it)
            }
        )
    }
}

@Composable
private fun TimerPickerTextField(
    time: String,
    onTimeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue =
        if (isFocused) ThemeColor.background.copy(alpha = 0.5f)
        else ThemeColor.tertiaryContainer,
        label = "backgroundColor"
    )

    BasicTextField(
        value = time,
        onValueChange = {
            if (it.length < 3) {
                onTimeChanged(it)
            }
            if (it.length > 2) {
                onTimeChanged(it.takeLast(1))
            } else if (it.length > 1) {
                focusManager.clearFocus()
            }
        },
        textStyle = TextStyle(
            fontSize = 60.sp,
            textAlign = TextAlign.Center,
            color = ThemeColor.onBackground,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, ThemeColor.tertiary, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .width(90.dp)
            .onFocusChanged { state ->
                isFocused = state.isFocused
                if(state.isFocused) {
                    onTimeChanged("")
                }
            }
    )
}


@Preview
@Composable
fun PreviewTimerPickerComponent() {
    PomoTasksTheme {
        var minutes by remember {
            mutableStateOf("")
        }

        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            TimerPickerComponent(
                minutes = minutes,
                onMinutesChanged = { minutes = it },
            )
        }
    }
}