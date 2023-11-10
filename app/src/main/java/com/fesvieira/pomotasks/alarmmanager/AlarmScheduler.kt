package com.fesvieira.pomotasks.alarmmanager

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}