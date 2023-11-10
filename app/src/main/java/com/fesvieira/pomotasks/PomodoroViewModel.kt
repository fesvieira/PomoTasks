package com.fesvieira.pomotasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fesvieira.pomotasks.alarmmanager.AlarmItem
import com.fesvieira.pomotasks.alarmmanager.AndroidAlarmScheduler
import com.fesvieira.pomotasks.ui.components.ClockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

class PomodoroViewModel(
    private val alarmScheduler: AndroidAlarmScheduler
): ViewModel() {

    private var alarmTime: LocalDateTime? = null
    private var alarmItem: AlarmItem? = null

    private val _clockState = MutableStateFlow(ClockState.STOPPED)
    val clockState get() = _clockState

    private val _seconds = MutableStateFlow(25 * 60L)
    val seconds: StateFlow<Long> = _seconds

    private val _totalSeconds = MutableStateFlow(25 * 60L)
    val totalSeconds: StateFlow<Long> = _totalSeconds

    private var timerJob: Job? = null

    private fun scheduleAlarm() {
        alarmTime = LocalDateTime.now().plusSeconds(_seconds.value + 1L)
        alarmTime?.let { alarmItem = AlarmItem(it) }

        alarmItem?.let { alarmScheduler.schedule(it) }
    }

    fun setClockState(state: ClockState) {
        viewModelScope.launch(Dispatchers.Default) {
            _clockState.value = state

            when (state) {
                ClockState.PAUSED, ClockState.STOPPED -> {
                    timerJob?.cancel()
                    alarmItem?.let(alarmScheduler::cancel)
                }

                ClockState.PLAYING -> {
                    scheduleAlarm()
                    timerJob = launch {
                        while (_seconds.value > 0) {
                            delay(1000)
                            _seconds.value =
                                Duration.between(
                                    LocalDateTime.now(),
                                    alarmTime
                                ).seconds
                        }
                    }
                }
            }
        }
    }

    fun setSeconds(seconds: Long) {
        _seconds.value = seconds
    }

    fun setTotalSeconds(seconds: Long) {
        _totalSeconds.value = seconds
    }
}