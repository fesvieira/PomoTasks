package com.fesvieira.pomotasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme.colorScheme as mtc
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fesvieira.pomotasks.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditDialog(
    onDismiss: () -> Unit,
    onAddTask: (String) -> Unit
) {
    var taskName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(mtc.primaryContainer, RoundedCornerShape(32.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Task Editor",
                style = Typography.labelMedium,
                color = mtc.onBackground
            )

            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                shape = RoundedCornerShape(32.dp),
                textStyle = Typography.bodyMedium,
                label = { Text(text = "Task name") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                keyboardActions = KeyboardActions { onAddTask(taskName) }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Button(onClick = { onAddTask(taskName) }) {
                    Text(text = "Ok")
                }

                Button(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}