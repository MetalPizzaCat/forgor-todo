package com.metalpizzacat.forgor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.metalpizzacat.forgor.data.TaskItem
import com.metalpizzacat.forgor.data.TaskRepository

class TaskViewModel(
    val taskRepository: TaskRepository
) : ViewModel() {
    fun getTasks(completed: Boolean): LiveData<List<TaskItem>> =
        taskRepository.getTasks(completed).asLiveData()
}