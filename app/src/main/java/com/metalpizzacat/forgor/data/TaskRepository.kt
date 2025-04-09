package com.metalpizzacat.forgor.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao : TaskDao) {
    val allTasks : Flow<List<TaskItem>>
        get() = taskDao.getAll()

    fun getTasks(completed : Boolean) : Flow<List<TaskItem>> = taskDao.getCompleted(completed)

    fun getTaskById(id : Int) : TaskItem? = taskDao.getTaskById(id)

    @WorkerThread
    fun insert(task : TaskItem){
        taskDao.insertAll(task)
    }

    @WorkerThread
    fun update(task : TaskItem){
        taskDao.update(task)
    }

    @WorkerThread
    fun delete(task : TaskItem){
        taskDao.delete(task)
    }
}