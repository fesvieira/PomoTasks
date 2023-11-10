package com.fesvieira.pomotasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fesvieira.pomotasks.alarmmanager.AlarmItem
import com.fesvieira.pomotasks.alarmmanager.AndroidAlarmScheduler
import com.fesvieira.pomotasks.repositories.UserPreferencesRepository
import com.fesvieira.pomotasks.ui.components.ClockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class PomodoroViewModel(
    private val alarmScheduler: AndroidAlarmScheduler,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private var alarmTime: LocalDateTime? = null
    var alarmItem: AlarmItem? = null

    private val _clockState = MutableStateFlow(ClockState.STOPPED)
    val clockState get() = _clockState

    private val _millis = MutableStateFlow(25 * 60000L)
    val millis: StateFlow<Long> = _millis

    private val _totalMillis = MutableStateFlow(25 * 60000L)
    val totalMillis: StateFlow<Long> = _totalMillis

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val lastAlarmMillis = userPreferencesRepository.lastAlarmMillis.first()
            val lastClockState = userPreferencesRepository.lastClockState.first()
            if (lastAlarmMillis != -1L && lastClockState == ClockState.PLAYING.name) {
                alarmTime = LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(lastAlarmMillis), ZoneId.systemDefault())

                alarmTime?.let {
                    alarmItem = AlarmItem(it)
                    _millis.value = Duration.between(LocalDateTime.now(), it).seconds
                }

                _totalMillis.value = userPreferencesRepository.lastAlarmTotalMillis.first()
                clockState.value = ClockState.valueOf(lastClockState)
            }
        }
    }

    private fun scheduleAlarm() {
        alarmTime = LocalDateTime.now().plusSeconds((_millis.value / 1000) + 1L)
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
                    alarmItem = null
                }

                ClockState.PLAYING -> {
                    scheduleAlarm()
                    timerJob = launch {
                        while (_millis.value > 0) {
                            delay(50)
                            _millis.value =
                                Duration.between(
                                    LocalDateTime.now(),
                                    alarmTime
                                ).toMillis()

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
}