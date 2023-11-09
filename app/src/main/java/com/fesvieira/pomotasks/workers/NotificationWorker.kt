package com.fesvieira.pomotasks.workers

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import androidx.compose.ui.res.integerArrayResource
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat.getActivity
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fesvieira.pomotasks.MainActivity
import com.fesvieira.pomotasks.R

class NotificationWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    companion object{
        const val NOTIFICATION_ID = "timer_alarm"
        const val NOTIFICATION_CHANNEL = "Pomodoro Alarm"
        const val NOTIFICATION_NAME = "PomoTasks Alarm"
    }

    override fun doWork(): Result {

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, "1")
        val pendingIntent = getActivity(applicationContext, 0, intent, 0, false)

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(NOTIFICATION_NAME)
            .setContentText("Time is over!!!")
            .setDefaults(DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notification.priority = IMPORTANCE_HIGH
        notification.setChannelId(NOTIFICATION_CHANNEL)

        val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes
            .Builder()
            .setUsage(USAGE_NOTIFICATION_RINGTONE)
            .setContentType(CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_CHANNEL,
            IMPORTANCE_HIGH
        )

        channel.setSound(ringtoneManager, audioAttributes)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(1, notification.build())

        return Result.success()
    }
}