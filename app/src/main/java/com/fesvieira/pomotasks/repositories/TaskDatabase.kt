package com.fesvieira.pomotasks.repositories

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fesvieira.pomotasks.data.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
}