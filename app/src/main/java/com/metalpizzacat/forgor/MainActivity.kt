package com.metalpizzacat.forgor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.metalpizzacat.forgor.data.TaskDatabase
import com.metalpizzacat.forgor.data.TaskRepository
import com.metalpizzacat.forgor.ui.theme.ForgorTheme
import com.metalpizzacat.forgor.viewmodel.TaskViewModel
import com.metalpizzacat.forgor.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private val taskDatabase by lazy { TaskDatabase.getDatabase(this) }
    private val taskRepository by lazy {
        TaskRepository(
            taskDatabase.taskDao()
        )
    }

    private val appViewModel: TaskViewModel by lazy {
        ViewModelProvider(
            this,
            TaskViewModelFactory(taskRepository)
        )[TaskViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ForgorApp(
                        appViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
