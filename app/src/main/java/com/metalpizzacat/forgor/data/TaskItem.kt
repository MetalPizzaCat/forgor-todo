package com.metalpizzacat.forgor.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "task")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val uid: Int?,

    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "deadline")
    val date: Date?,

    @ColumnInfo(name = "completed")
    val completed: Boolean
)
