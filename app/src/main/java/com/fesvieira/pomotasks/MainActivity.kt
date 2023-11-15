package com.fesvieira.pomotasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fesvieira.pomotasks.repositories.UserPreferencesRepository
import com.fesvieira.pomotasks.ui.screens.MainScreen
import com.fesvieira.pomotasks.ui.theme.PomoTasksTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val pomodoroViewModel: PomodoroViewModel by viewModels()

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

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
        pomodoroViewModel.saveState()
        super.onStop()
    }
}