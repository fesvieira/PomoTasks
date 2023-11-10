package com.fesvieira.pomotasks.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fesvieira.pomotasks.ui.components.ClockState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    // KEYS
    private companion object {
        val LAST_CLOCK_STATE = stringPreferencesKey("last_clock_state")
        val LAST_ALARM_TIMESTAMP = longPreferencesKey("last_alarm_timestamp")
        val LAST_ALARM_TOTAL_MILLIS = longPreferencesKey("last_alarm_total_millis")
    }

    // GETTERS
    val lastClockState: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[LAST_CLOCK_STATE] ?: ClockState.PAUSED.name
        }

    val lastAlarmTimeStamp: Flow<Long> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[LAST_ALARM_TIMESTAMP] ?: -1
        }

    val lastAlarmTotalMillis: Flow<Long> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[LAST_ALARM_TOTAL_MILLIS] ?: -1
        }

    // SETTERS
    suspend fun saveLastClockState(state: ClockState) {
        dataStore.edit { preferences ->
            preferences[LAST_CLOCK_STATE] = state.name
        }
    }

    suspend fun setLastAlarmTimeStamp(millis: Long?) {
        dataStore.edit { preferences ->
            preferences[LAST_ALARM_TIMESTAMP] = millis ?: -1
        }
    }

    suspend fun setLastAlarmTotalMillis(millis: Long?) {
        dataStore.edit { preferences ->
            preferences[LAST_ALARM_TOTAL_MILLIS] = millis ?: -1
        }
    }
}