package com.fesvieira.pomotasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TASK_LIST")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val isDone: Boolean,
    val timeStamp: Long
)
