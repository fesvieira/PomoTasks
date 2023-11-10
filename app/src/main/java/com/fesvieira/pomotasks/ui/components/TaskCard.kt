package com.fesvieira.pomotasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme.colorScheme as mtc
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import com.fesvieira.pomotasks.ui.theme.Typography

@Composable
fun TaskCard(
    title: String,
    isDone: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(mtc.primaryContainer, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = Typography.labelLarge,
            color = mtc.onPrimaryContainer
        )

        Checkbox(
            checked = isDone,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview
@Composable
fun TaskCardPreview() {
    Column {
        PomoTasksTheme {
            TaskCard(
                title = "Task1",
                isDone = false,
                onCheckedChange = {}
            )
        }

        PomoTasksTheme {
            PomoTasksTheme(darkTheme = true) {
                TaskCard(
                    title = "Task1",
                    isDone = true,
                    onCheckedChange = {}
                )
            }
        }
    }
}