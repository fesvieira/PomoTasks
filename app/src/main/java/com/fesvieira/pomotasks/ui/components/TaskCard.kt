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
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(mtc.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = Typography.labelLarge,
            color = mtc.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = isDone,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(48.dp)
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
                    title = "Very long task name that will probably take much more than one single line",
                    isDone = true,
                    onCheckedChange = {}
                )
            }
        }
    }
}