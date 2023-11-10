package com.fesvieira.pomotasks.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.fesvieira.pomotasks.alarmmanager.AndroidAlarmScheduler
import com.fesvieira.pomotasks.repositories.TaskDao
import com.fesvieira.pomotasks.repositories.TaskDatabase
import com.fesvieira.pomotasks.repositories.TaskRepository
import com.fesvieira.pomotasks.repositories.TaskRepositoryImpl
import com.fesvieira.pomotasks.repositories.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideTaskDb(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        TaskDatabase::class.java,
        "TASK_TABLE"
    ).build()

    @Provides
    fun providesTaskDao(
        taskDb: TaskDatabase
    ) = taskDb.taskDao()

    @Provides
    fun provideTaskRepository(
        taskDao: TaskDao
    ): TaskRepository = TaskRepositoryImpl(taskDao)

    @Provides
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository = UserPreferencesRepository(context.dataStore)

    @Provides
    fun alarmScheduler(
        @ApplicationContext context: Context
    ): AndroidAlarmScheduler = AndroidAlarmScheduler(context)
}