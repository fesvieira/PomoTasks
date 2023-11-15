package com.fesvieira.pomotasks.alarmmanager

import java.time.LocalDateTime

interface AlarmScheduler {
    fun schedule(time: LocalDateTime)
    fun cancel()
}