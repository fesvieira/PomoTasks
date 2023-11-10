package com.fesvieira.pomotasks.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fesvieira.pomotasks.workers.NotificationService


class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationService = NotificationService

        context?.let {
            notificationService.scheduleNotification(context)
        }
    }
}