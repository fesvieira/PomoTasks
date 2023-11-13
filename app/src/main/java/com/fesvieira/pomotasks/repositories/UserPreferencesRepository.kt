package com.fesvieira.pomotasks.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    // KEYS
    private companion object {
        val LAST_ALARM_TIMESTAMP = longPreferencesKey("last_alarm_timestamp")
        val LAST_ALARM_TOTAL_MILLIS = longPreferencesKey("last_alarm_total_millis")
    }

    // GETTERS
    val lastAlarmTimeStamp: Flow<Long?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[LAST_ALARM_TIMESTAMP]
        }

    val lastAlarmTotalMillis: Flow<Long> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[LAST_ALARM_TOTAL_MILLIS] ?: 1500000L
        }

    // SETTERS
    suspend fun setLastAlarmTimeStamp(millis: Long?) {
        dataStore.edit { preferences ->
            if (millis != null) {
                preferences[LAST_ALARM_TIMESTAMP] = millis
            } else {
                preferences.remove(LAST_ALARM_TIMESTAMP)
            }
        }
    }

    suspend fun setLastAlarmTotalMillis(millis: Long?) {
        dataStore.edit { preferences ->
            millis?.let { safeMillis ->
                preferences[LAST_ALARM_TOTAL_MILLIS] = safeMillis
                return@edit
            }
            preferences.remove(LAST_ALARM_TOTAL_MILLIS)
        }
    }
}