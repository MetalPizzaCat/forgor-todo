package com.metalpizzacat.forgor.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasks(completed: Boolean): Flow<List<TaskItem>> = taskDao.getCompleted(completed)

    fun getTasksThatContainText(completed: Boolean, text: String): Flow<List<TaskItem>> =
        taskDao.getContainingTextAndCompleted(completed, text)

    @WorkerThread
    fun insert(task: TaskItem) {
        taskDao.insertAll(task)
    }

    @WorkerThread
    fun update(task: TaskItem) {
        taskDao.update(task)
    }

    @WorkerThread
    fun delete(task: TaskItem) {
        taskDao.delete(task)
    }
}