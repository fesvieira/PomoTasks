package com.fesvieira.pomotasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fesvieira.pomotasks.data.Task
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import com.fesvieira.pomotasks.ui.theme.Typography
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@Composable
fun TaskEditDialog(
    editTask: Task? = null,
    onDismiss: () -> Unit,
    onAddTask: (String) -> Unit,
    onEditTask: (Task) -> Unit
) {
    var taskName by remember { mutableStateOf(editTask?.name ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(mtc.surfaceContainer, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Task Editor",
                style = Typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                color = mtc.onBackground
            )

            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                shape = RoundedCornerShape(8.dp),
                textStyle = Typography.bodyMedium,
                label = { Text(text = "Task name") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                keyboardActions = KeyboardActions { onAddTask(taskName) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = mtc.primary,
                    unfocusedBorderColor = mtc.primary,
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp, alignment = Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ok",
                    style = Typography.labelMedium,
                    color = if (taskName.isNotEmpty()) mtc.primary else mtc.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .then (
                            if (taskName.isNotEmpty()) {
                                Modifier.clickable {
                                    if (editTask != null)
                                        onEditTask(editTask.copy(name = taskName))
                                    else
                                        onAddTask(taskName)
                                }
                            } else {
                                Modifier
                            }
                        )
                        .padding(8.dp)
                )

                Text(
                    text = "Cancel",
                    style = Typography.labelMedium,
                    color = mtc.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onDismiss()
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTAskDialog() {
    PomoTasksTheme {
        TaskEditDialog(onDismiss = { }, onAddTask = {}, onEditTask = {})
    }
}