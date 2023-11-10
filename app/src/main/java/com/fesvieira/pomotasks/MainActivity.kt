package com.fesvieira.pomotasks

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.fesvieira.pomotasks.alarmmanager.AndroidAlarmScheduler
import com.fesvieira.pomotasks.repositories.UserPreferencesRepository
import com.fesvieira.pomotasks.ui.components.ClockState
import com.fesvieira.pomotasks.ui.screens.MainScreen
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class MainActivity : ComponentActivity() {
    private lateinit var alarmScheduler: AndroidAlarmScheduler
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    private val pomodoroViewModel: PomodoroViewModel by lazy {
        alarmScheduler = AndroidAlarmScheduler(this)
        userPreferencesRepository = UserPreferencesRepository(this.dataStore)
        PomodoroViewModel(
            alarmScheduler = alarmScheduler,
            userPreferencesRepository = userPreferencesRepository
        )
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

    override fun onStop() {
        lifecycleScope.launch {
            val clockState = pomodoroViewModel.clockState.value
            userPreferencesRepository.setLastAlarmTotalMillis(
                pomodoroViewModel.totalMillis.value
            )
            userPreferencesRepository.saveLastClockState(clockState)
            if (clockState != ClockState.PLAYING) {
                userPreferencesRepository.setLastAlarmTimeStamp(-1)
            } else {
                pomodoroViewModel.alarmItem?.let {
                    userPreferencesRepository.setLastAlarmTimeStamp(
                        it.time.toEpochSecond(
                            OffsetDateTime.now().offset
                        ) * 1000
                    )
                }
            }
        }
        super.onStop()
    }
}