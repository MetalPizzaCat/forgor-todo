package com.metalpizzacat.forgor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.metalpizzacat.forgor.data.TaskItem
import com.metalpizzacat.forgor.data.TaskRepository

class TaskViewModel(
    val taskRepository: TaskRepository
) : ViewModel() {
    fun getTasks(completed: Boolean, searchText: String?): LiveData<List<TaskItem>> =
        if (searchText == null) {
            taskRepository.getTasks(completed).asLiveData()
        } else {
            taskRepository.getTasksThatContainText(completed, searchText).asLiveData()
        }
}