package com.fesvieira.pomotasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fesvieira.pomotasks.alarmmanager.AndroidAlarmScheduler
import com.fesvieira.pomotasks.data.Task
import com.fesvieira.pomotasks.repositories.TaskRepository
import com.fesvieira.pomotasks.repositories.UserPreferencesRepository
import com.fesvieira.pomotasks.ui.components.ClockState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val alarmScheduler: AndroidAlarmScheduler,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private var alarmTime: LocalDateTime? = null

    private val _clockState = MutableStateFlow(ClockState.PAUSED)
    val clockState get() = _clockState

    private val _millis = MutableStateFlow(1500000L)
    val millis: StateFlow<Long> = _millis

    private val _totalMillis = MutableStateFlow(1500000L)
    val totalMillis: StateFlow<Long> = _totalMillis

    private val _tasksListStateFlow = MutableStateFlow<List<Task>>(emptyList())
    val tasksListStateFlow get() = _tasksListStateFlow
    
    private val _isLoadingTasks = MutableStateFlow(true)
    val isLoadingTasks get() = _isLoadingTasks.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadState()

        viewModelScope.launch {
            taskRepository.getTasks().collect {
                _tasksListStateFlow.value = it
                _isLoadingTasks.value = false
            }
        }
    }

    private fun scheduleAlarm() {
        alarmTime = LocalDateTime.now().plusSeconds(_millis.value / 1000)
        alarmTime?.let { alarmScheduler.schedule(it)  }
    }

    fun setClockState(state: ClockState, scheduleAlarm: Boolean = true) {
        viewModelScope.launch(Dispatchers.Default) {
            _clockState.value = state

            when (state) {
                ClockState.PAUSED, ClockState.STOPPED -> {
                    timerJob?.cancel()
                    alarmScheduler.cancel()
                }

                ClockState.PLAYING -> {
                    if (scheduleAlarm) scheduleAlarm()

                    timerJob = launch {
                        while (_millis.value > 0) {
                            delay(50)
                            _millis.value =
                                Duration.between(LocalDateTime.now(), alarmTime).toMillis()

                            if (millis.value <= 0L) {
                                _clockState.value = ClockState.STOPPED
                                _millis.value = _totalMillis.value
                            }
                        }
                    }
                }
            }
        }
    }

    fun setMillis(millis: Long) {
        _millis.value = millis
    }

    fun setTotalMillis(millis: Long) {
        _totalMillis.value = millis
    }

    fun addTask(taskName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(
                id = 0,
                name = taskName,
                isDone = false,
                timeStamp = LocalDateTime.now().toEpochSecond(OffsetDateTime.now().offset) * 1000
            )
            taskRepository.addTask(task)
        }
    }

    fun reAddTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.addTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.deleteTask(task)
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask(task)
        }
    }

    fun toggleTaskDone(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask(task.copy(isDone = !task.isDone))
        }
    }

    fun saveState() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.apply {
                setLastAlarmTotalMillis(totalMillis.value)

                if (clockState.value == ClockState.PLAYING && alarmTime != null) {
                    setLastAlarmTimeStamp(
                        (alarmTime?.toEpochSecond(OffsetDateTime.now().offset) ?: 0) * 1000
                    )
                } else {
                    setLastAlarmTimeStamp(null)
                }
            }
        }
    }

    private fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            _totalMillis.value = userPreferencesRepository.lastAlarmTotalMillis.first()
            val lastAlarmStamp = userPreferencesRepository.lastAlarmTimeStamp.first()
            val currentTimeStamp = LocalDateTime.now()
                .toEpochSecond(OffsetDateTime.now().offset) * 1000

            if (lastAlarmStamp == null || lastAlarmStamp < currentTimeStamp) {
                alarmTime = LocalDateTime.now().plusNanos(_totalMillis.value * 1000)
                setClockState(ClockState.PAUSED, false)
                _millis.value = totalMillis.value
            } else {
                alarmTime = LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(lastAlarmStamp), ZoneId.systemDefault())

                setClockState(ClockState.PLAYING, false)
            }
        }
    }
}