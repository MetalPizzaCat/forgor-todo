package com.metalpizzacat.forgor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAll(): Flow<List<TaskItem>>

    @Query("SELECT * FROM task WHERE completed = :completed")
    fun getCompleted(completed: Boolean): Flow<List<TaskItem>>

    @Query("select * from task where completed= :completed and text like '%' || :taskText || '%'")
    fun getContainingTextAndCompleted(completed: Boolean, taskText: String?): Flow<List<TaskItem>>

    @Query("SELECT * FROM task WHERE uid = :id")
    fun getTaskById(id: Int): TaskItem?

    @Insert
    fun insertAll(vararg tasks: TaskItem)

    @Update
    fun update(task: TaskItem)

    @Delete
    fun delete(task: TaskItem)
}