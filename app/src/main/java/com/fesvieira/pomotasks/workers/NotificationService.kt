package com.fesvieira.pomotasks.workers

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object NotificationService {
    fun scheduleNotification(
        context: Context
    ) {
        val tag = "PomodoroAlarm"
        val instanceWorkManager = WorkManager.getInstance(context)

        val request =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .addTag(tag)
                .build()

        instanceWorkManager.enqueue(request)
    }
}