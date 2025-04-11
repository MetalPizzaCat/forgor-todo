package com.metalpizzacat.forgor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metalpizzacat.forgor.data.TaskItem
import com.metalpizzacat.forgor.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgorApp(viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var isInCompletedTasks by remember { mutableStateOf(false) }
    var editedTask by remember { mutableStateOf<TaskItem?>(null) }
    val scope = rememberCoroutineScope()
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { showBottomSheet = true }) {
            Icon(Icons.Default.Add, "Add new task icon")
        }
    }) { padding ->
        Column(modifier = modifier.padding(padding)) {
            TopAppBar(isInCompleted = isInCompletedTasks) {
                isInCompletedTasks = it
            }

            TaskList(
                viewModel,
                isInCompletedTasks,
                editRequested = {
                    editedTask = it
                    showBottomSheet = true
                }
            ) { toggledTask ->
                scope.launch(Dispatchers.IO) {
                    viewModel.taskRepository.update(toggledTask)
                }
            }

        }
        AnimatedVisibility(visible = showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    editedTask = null
                },
                modifier = Modifier.fillMaxSize(),
                sheetState = rememberModalBottomSheetState()
            ) {
                TaskEditor(task = editedTask, saveRequested = {
                    scope.launch(Dispatchers.IO) {
                        if (editedTask == null) {
                            viewModel.taskRepository.insert(it)
                        } else {
                            viewModel.taskRepository.update(it)
                        }
                        showBottomSheet = false
                        editedTask = null
                    }
                }, deleteRequested = {
                    scope.launch(Dispatchers.IO) {
                        if (editedTask != null) {
                            viewModel.taskRepository.delete(editedTask!!)
                        }
                    }
                }, canceled = {
                    showBottomSheet = false
                    editedTask = null
                }, modifier.padding(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun TaskEditor(
    task: TaskItem?,
    saveRequested: (TaskItem) -> Unit,
    deleteRequested: () -> Unit,
    canceled: () -> Unit,
    modifier: Modifier = Modifier
) {
    var taskText by remember { mutableStateOf(task?.text ?: "") }
    var hasDeadline by remember { mutableStateOf(task?.date != null) }
    var deadline by remember { mutableStateOf<Date?>(task?.date) }
    var pickingDeadline by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Row {
            IconButton(onClick = { canceled() }) {
                Icon(Icons.Default.Close, contentDescription = "Cancel edit icon")
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {
                if (taskText.isEmpty()) {
                    deleteRequested()
                } else {
                    saveRequested(
                        TaskItem(
                            task?.uid,
                            taskText,
                            if (hasDeadline) {
                                deadline
                            } else {
                                null
                            },
                            false
                        )
                    )
                }
            }) {
                if (taskText.isNotEmpty() || task != null) {
                    Icon(
                        if (taskText.isNotEmpty()) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.Delete
                        }, contentDescription = "Save edit icon"
                    )
                }
            }
        }
        OutlinedTextField(value = taskText, onValueChange = { taskText = it }, label = {
            Text(
                text = stringResource(
                    R.string.task
                )
            )
        })
        Row(Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = hasDeadline, onCheckedChange = { hasDeadline = it })
            Text(text = stringResource(R.string.has_deadline))
            AnimatedVisibility(visible = hasDeadline) {
                ElevatedCard(
                    onClick = { pickingDeadline = true },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(
                        text = if (deadline == null) {
                            stringResource(R.string.pick_deadline)
                        } else {
                            SimpleDateFormat(
                                "EEE dd-MM-yyyy",
                                Locale.getDefault()
                            ).format(deadline!!)
                        },
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

        }

        AnimatedVisibility(visible = pickingDeadline) {
            DatePickerModal(onDismiss = { pickingDeadline = false }) { date ->
                deadline = if (date == null) {
                    null
                } else {
                    Date(date)
                }
            }
        }
    }
}


@Composable
fun TopAppBar(
    isInCompleted: Boolean,
    modifier: Modifier = Modifier,
    onSelected: (completedTasks: Boolean) -> Unit
) {
    TabRow(
        selectedTabIndex = if (isInCompleted) {
            0
        } else {
            1
        }, modifier = modifier
    ) {
        Tab(selected = !isInCompleted, onClick = { onSelected(false) }, icon = {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Unfinished tasks icon")
        })
        Tab(selected = isInCompleted, onClick = { onSelected(true) }, icon = {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Finished tasks icon")
        })
    }
}

@Composable
fun TaskList(
    viewModel: TaskViewModel,
    completed: Boolean,
    modifier: Modifier = Modifier,
    editRequested: (task: TaskItem) -> Unit,
    taskToggled: (task: TaskItem) -> Unit
) {
    val tasks by viewModel.getTasks(completed).observeAsState()

    tasks?.let { loadedTasks ->
        LazyColumn(modifier = modifier) {
            items(loadedTasks, key = { it.uid ?: 0 }) { task ->
                TaskRow(
                    text = task.text,
                    date = task.date,
                    completed = task.completed,
                    selected = { editRequested(task) }
                ) {
                    taskToggled(task.copy(completed = !task.completed))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskRow(
    text: String,
    date: Date?,
    completed: Boolean,
    modifier: Modifier = Modifier,
    selected: () -> Unit,
    toggled: (value: Boolean) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .combinedClickable(onClick = {}, onLongClick = { selected() })
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            Checkbox(checked = completed, onCheckedChange = { toggled(!completed) })
            Text(
                text = text,
                fontSize = 18.sp,
                textDecoration = if (completed) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )
        }
        date?.let { date ->
            Row {
                ElevatedCard(
                    onClick = { /*do nothing, for sake of simplicity*/ },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Row(modifier = Modifier.padding(5.dp)) {
                        Text(text = "Due at: ")
                        Text(
                            text = SimpleDateFormat(
                                "EEE dd-MM-yyyy",
                                Locale.getDefault()
                            ).format(date)
                        )
                    }
                }

                RemainingDaysDisplay(date = date)

            }
        }

    }
}

@Composable
fun RemainingDaysDisplay(date: Date, modifier: Modifier = Modifier) {
    val daysLeft = ChronoUnit.DAYS.between(
        LocalDate.now(),
        date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    )

    val flavourText = if (daysLeft < 0) {
        "ago"
    } else {
        "left"
    }
    ElevatedCard(
        onClick = { /*do nothing, for sake of simplicity*/ },
        modifier = modifier.padding(5.dp)
    ) {
        Text(
            text =
            if (daysLeft == 0L) {
                "Today"
            } else if (daysLeft.absoluteValue <= 90) {
                daysLeft.absoluteValue.toString() + stringResource(R.string.days) + flavourText
            } else if (daysLeft.absoluteValue <= 400) {
                val monthsLeft = ChronoUnit.MONTHS.between(
                    LocalDate.now(),
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                )
                monthsLeft.absoluteValue.toString() + stringResource(R.string.months) + flavourText
            } else {
                val yearsLeft = ChronoUnit.YEARS.between(
                    LocalDate.now(),
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                )
                yearsLeft.absoluteValue.toString() + " years " + flavourText
            }, modifier = Modifier.padding(5.dp)
        )
    }
}

@Preview
@Composable
fun TaskPreview() {
    TaskRow(
        text = "Hello world\nLong note\nLonger text\nHello world\n" +
                "Long note\n" +
                "Longer text",
        date = Date(10000000),
        completed = false,
        selected = { }) {

    }
}

@Preview
@Composable
fun EditorPreview() {
    TaskEditor(
        task = null,
        saveRequested = {},
        deleteRequested = { /*TODO*/ },
        canceled = { /*TODO*/ })
}

