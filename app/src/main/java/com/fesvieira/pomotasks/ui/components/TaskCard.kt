package com.fesvieira.pomotasks.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import com.fesvieira.pomotasks.ui.theme.Typography
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@Composable
fun TaskCard(
    title: String,
    isDone: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
    onTaskClick: (() -> Unit)? = null,
) {
    val colorAlpha by animateFloatAsState(targetValue = if (isDone) 0.6f else 1f, label = "colorAlpha")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .then(
                if (onTaskClick != null) Modifier.clickable { onTaskClick() }
                else Modifier
            )
            .fillMaxWidth()
            .background(mtc.surfaceVariant.copy(alpha = colorAlpha), RoundedCornerShape(8.dp))
            .padding(16.dp)

    ) {
        Text(
            text = buildTaskName(taskName = title, isDone = isDone),
            style = Typography.bodyLarge,
            color = mtc.onPrimaryContainer.copy(alpha = colorAlpha),
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = isDone,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(48.dp)
        )
    }
}

fun buildTaskName(taskName: String, isDone: Boolean) = buildAnnotatedString {
    withStyle(
        style = SpanStyle(
            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
        )
    ) {
        append(taskName)
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
                onTaskClick = {},
                onCheckedChange = {}
            )
        }

        PomoTasksTheme {
            PomoTasksTheme(darkTheme = true) {
                TaskCard(
                    title = "Very long task name that will probably take much more than one single line",
                    isDone = true,
                    onTaskClick = {},
                    onCheckedChange = {}
                )
            }
        }
    }
}