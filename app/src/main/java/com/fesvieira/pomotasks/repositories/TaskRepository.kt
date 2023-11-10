package com.fesvieira.pomotasks.repositories

import com.fesvieira.pomotasks.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasks(): Flow<List<Task>>

    suspend fun getTaskById(id: Int): Flow<Task>

    suspend fun addTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)
}