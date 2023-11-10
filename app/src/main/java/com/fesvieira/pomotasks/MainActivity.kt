package com.fesvieira.pomotasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fesvieira.pomotasks.alarmmanager.AndroidAlarmScheduler
import com.fesvieira.pomotasks.ui.screens.MainScreen
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme

class MainActivity : ComponentActivity() {
    private lateinit var alarmScheduler: AndroidAlarmScheduler
    private val pomodoroViewModel: PomodoroViewModel by lazy {
        alarmScheduler = AndroidAlarmScheduler(this)
        PomodoroViewModel(alarmScheduler = alarmScheduler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PomoTasksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                     MainScreen(pomodoroViewModel)
                }
            }
        }
    }
}