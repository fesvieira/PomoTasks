package com.fesvieira.pomotasks.ui.screens

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.PomodoroViewModel
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.helpers.isAllowedTo
import com.fesvieira.pomotasks.ui.components.AppFloatActionButton
import com.fesvieira.pomotasks.ui.components.ClockComponent
import com.fesvieira.pomotasks.ui.components.ClockState
import com.fesvieira.pomotasks.ui.components.TaskCard
import com.fesvieira.pomotasks.ui.components.TaskEditDialog
import com.fesvieira.pomotasks.ui.theme.mtl_error
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    pomodoroViewModel: PomodoroViewModel
) {
    val context = LocalContext.current
    val clockState by pomodoroViewModel.clockState.collectAsState()
    val totalMillis by pomodoroViewModel.totalMillis.collectAsState()
    val millis by pomodoroViewModel.millis.collectAsState()
    val tasks by pomodoroViewModel.tasksListStateFlow.collectAsState()
    var showTaskEditDialog by remember { mutableStateOf(false) }

    val permissionsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { allowed ->
            if (allowed) {
                pomodoroViewModel.setClockState(ClockState.PLAYING)
            } else {
                Toast.makeText(
                    context,
                    "We need permission to play Alarms",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = mtc.background
    ) {
        Scaffold(
            floatingActionButton = {
                AppFloatActionButton(icon = painterResource(R.drawable.ic_add)) {
                    showTaskEditDialog = true
                }
            }
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(mtc.background)
            ) {
                item {
                    ClockComponent(
                        clockState = clockState,
                        millis = millis,
                        totalMillis = totalMillis,
                        onMinutesChange = { minutesString ->
                            val newValue = (minutesString.toLongOrNull() ?: 0L) * 60000L
                            pomodoroViewModel.setTotalMillis(newValue)
                            pomodoroViewModel.setMillis(newValue)
                        },
                        onClockStateChange = { newClockState ->
                            if (
                                newClockState == ClockState.PLAYING &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            ) {
                                if (context.isAllowedTo(POST_NOTIFICATIONS)) {
                                    pomodoroViewModel.setClockState(newClockState)
                                } else {
                                    permissionsLauncher.launch(POST_NOTIFICATIONS)
                                }
                            } else {
                                pomodoroViewModel.setClockState(newClockState)
                            }
                        }
                    )
                }

                items(tasks) { task ->
                    val dismissState = rememberDismissState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == DismissValue.DismissedToStart) {
                                pomodoroViewModel.deleteTask(task)
                            }

                            true
                        },
                        positionalThreshold = { 0.3f }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .animateItemPlacement(tween(300)),
                        directions = setOf(DismissDirection.EndToStart),
                        background = { SwipeToDismissDynamicBackground(dismissState) },
                        dismissContent = {
                            TaskCard(
                                title = task.name,
                                isDone = task.isDone,
                                onCheckedChange = { pomodoroViewModel.toggleTaskDone(task)},
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        pomodoroViewModel.editTask(task)
                                    }
                            )
                        }
                    )
                }
            }

            AnimatedVisibility(visible = showTaskEditDialog) {
                TaskEditDialog(
                    onDismiss = { showTaskEditDialog = false },
                    onAddTask = {
                        taskName ->
                        pomodoroViewModel.addTask(taskName = taskName)
                        showTaskEditDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissDynamicBackground(dismissState: DismissState) {
    val isSwiping by remember(dismissState) {
        derivedStateOf { dismissState.dismissDirection == DismissDirection.EndToStart && dismissState.progress > 0.1f }
    }
    val color by animateColorAsState(
        when {
            isSwiping -> mtl_error
            else -> MaterialTheme.colorScheme.background
        }, label = "color"
    )

    val scale by animateFloatAsState(
        if (isSwiping) 1f else 0.001f,
        label = "scale"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "Delete icon",
            tint = Color.White,
            modifier = Modifier.scale(scale)
        )
    }
}