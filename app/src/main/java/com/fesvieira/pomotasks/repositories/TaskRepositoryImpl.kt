package com.fesvieira.pomotasks.repositories

import com.fesvieira.pomotasks.data.Task
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun getTasks(): Flow<List<Task>> {
        return taskDao.getTasks()
    }

    override suspend fun getTaskById(id: Int): Flow<Task> {
        return taskDao.getTaskById(id)
    }

    override suspend fun addTask(task: Task) {
        return taskDao.addTask(task)
    }

    override suspend fun updateTask(task: Task) {
        return taskDao.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        return taskDao.deleteTask(task)
    }
}