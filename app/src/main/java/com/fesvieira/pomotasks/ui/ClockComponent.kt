package com.fesvieira.pomotasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme

@Composable
fun ClockComponent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(
                brush = Brush.radialGradient(
                    with(MaterialTheme.colorScheme.onBackground) {
                        listOf(
                            this.copy(alpha = 1.0f),
                            this.copy(alpha = 0.8f),
                            this.copy(alpha = 0.5f),
                            this.copy(alpha = 0.3f),
                            this.copy(alpha = 0.01f),
                        )
                    },
                ),
                CircleShape
            )
            .padding(20.dp)
            .background(
                MaterialTheme.colorScheme.tertiaryContainer,
                CircleShape
            )
            .padding(16.dp)
            .size(240.dp)
            .border(10.dp, MaterialTheme.colorScheme.tertiary, CircleShape)

    ) {
        Text(
            text = "00:59",
            fontSize = 60.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
fun PreviewClockComponent() {
    val view: @Composable () -> Unit = {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)) {
            ClockComponent()
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