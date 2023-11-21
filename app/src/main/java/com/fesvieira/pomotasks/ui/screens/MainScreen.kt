package com.fesvieira.pomotasks.ui.screens

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.PomodoroViewModel
import com.fesvieira.pomotasks.R
import com.fesvieira.pomotasks.data.Task
import com.fesvieira.pomotasks.helpers.isAllowedTo
import com.fesvieira.pomotasks.ui.components.AppFloatActionButton
import com.fesvieira.pomotasks.ui.components.ClockComponent
import com.fesvieira.pomotasks.ui.components.ClockState
import com.fesvieira.pomotasks.ui.components.TaskCard
import com.fesvieira.pomotasks.ui.components.TaskEditDialog
import com.fesvieira.pomotasks.ui.theme.Typography
import com.fesvieira.pomotasks.ui.theme.mtl_error
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()
    var showDone by remember { mutableStateOf(true) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

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
                AppFloatActionButton(
                    icon = painterResource(R.drawable.ic_add),
                    isAnimating = tasks.isEmpty()
                ) {
                    selectedTask = null
                    showTaskEditDialog = true
                }
            }
        ) { paddingValues ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(mtc.background)
                    .animateContentSize()
            ) {
                stickyHeader {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(mtc.background)
                    ) {
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
                }

                item{
                    AnimatedVisibility(
                        tasks.isEmpty(),
                        enter = slideInHorizontally(),
                        exit = slideOutVertically { -it }
                    ) {
                        Text(
                            text = "Click on Add button to create new tasks.",
                            style = Typography.labelLarge,
                            color = mtc.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }

                items(tasks, key = { task -> task.id }) { task ->
                    val dismissState = rememberDismissState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == DismissValue.DismissedToStart) {
                                pomodoroViewModel.deleteTask(task)
                            }

                            true
                        },
                        positionalThreshold = { 0.3f }
                    )

                    AnimatedVisibility(
                        visible = !task.isDone,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut(),
                        modifier = Modifier.animateItemPlacement(
                            tween(400)
                        )
                    ) {
                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = { SwipeToDismissDynamicBackground(dismissState) },
                            dismissContent = {
                                TaskCard(
                                    title = task.name,
                                    isDone = task.isDone,
                                    onTaskClick = {
                                        selectedTask = task
                                        showTaskEditDialog = true
                                    },
                                    onCheckedChange = { pomodoroViewModel.toggleTaskDone(task) },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .clickable {
                                            pomodoroViewModel.editTask(task)
                                        }
                                )
                            },
                            modifier = Modifier.animateItemPlacement(animationSpec = tween(200))
                        )
                    }
                }

                item {
                    AnimatedVisibility(visible = tasks.any { it.isDone }) {
                        Text(
                            text = "Completed! ${if (showDone) " \uD83D\uDFE6" else " ⬛"}",
                            style = Typography.labelLarge,
                            color = mtc.onBackground,
                            modifier = Modifier
                                .padding(top = 40.dp)
                                .clickable {
                                    showDone = !showDone
                                }
                        )
                    }
                }

                items(tasks, key = { "${it.id}" }) { task ->
                    val dismissState = rememberDismissState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == DismissValue.DismissedToStart) {
                                pomodoroViewModel.deleteTask(task)
                            }

                            true
                        },
                        positionalThreshold = { 0.3f }
                    )

                    AnimatedVisibility(
                        visible = task.isDone && showDone,
                        enter = slideInVertically { -it } + fadeIn(),
                        exit = slideOutVertically { -it } + fadeOut(),
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(400)
                        )
                    ) {
                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = { SwipeToDismissDynamicBackground(dismissState) },
                            dismissContent = {
                                TaskCard(
                                    title = task.name,
                                    isDone = task.isDone,
                                    onCheckedChange = { pomodoroViewModel.toggleTaskDone(task) },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .clickable {
                                            pomodoroViewModel.editTask(task)
                                        }
                                )
                            },
                            modifier = Modifier.animateItemPlacement(animationSpec = tween(200))
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(200.dp))
                }
            }

            AnimatedVisibility(visible = showTaskEditDialog) {
                TaskEditDialog(
                    editTask = selectedTask,
                    onDismiss = { showTaskEditDialog = false },
                    onAddTask = { taskName ->
                        coroutineScope.launch {
                            showTaskEditDialog = false
                            delay(500)
                            pomodoroViewModel.addTask(taskName = taskName)
                        }
                    },
                    onEditTask = { task ->
                        coroutineScope.launch {
                            showTaskEditDialog = false
                            delay(500)
                            pomodoroViewModel.editTask(task)
                        }
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