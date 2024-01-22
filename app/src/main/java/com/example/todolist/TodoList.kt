package com.example.todolist


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class TodoListItem(val id:Int,val task:String,val isCompleted:Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoList(){
    var showDialog by remember { mutableStateOf(false) }
    var todoItem by remember { mutableStateOf(listOf<TodoListItem>()) }
    var taskTodo by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var todoId by remember { mutableStateOf(0) }
    val (_,todoComplete) = todoItem.partition { !it.isCompleted }
    Scaffold(
        modifier = Modifier.padding(10.dp),
        topBar = {AssistChipContainer(todoItem.size,todoComplete.size)},
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxWidth(),
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(0)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Button",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

        }
    ){
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = if(todoItem.isNotEmpty()) Arrangement.Top else Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(todoItem.isNotEmpty()){
                LazyColumn {
                    items(todoItem){
                            items ->
                        TodoCard(item = items,
                            editHandler = {
                                taskTodo = items.task
                                showDialog = true
                                isEditMode = true
                                todoId = items.id
                            },
                            deleteHandler = {
                                todoItem = todoItem - items
                            },
                            completeHandler = {
                                    isCompleteBoolean ->
//                        Log.i("CHECK",isCompleteBoolean.toString())
                                todoItem = todoItem.map { it.copy(isCompleted = if(items.id == it.id) isCompleteBoolean else it.isCompleted)}
                            }
                        )
                    }
                }
            }else{
                Text(text = "Task Is Empty", fontSize = 24.sp)
            }


            //dialog
            if(showDialog){
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            if(isEditMode){
                                todoItem = todoItem.map { it.copy(task = if(todoId == it.id)taskTodo else it.task)}
                            }else{
                                if(taskTodo.isNotBlank()){
                                    val id = todoItem.size + 1
                                    todoItem = todoItem + TodoListItem(id = id, task = taskTodo)
                                }
                            }

                            showDialog = false
                            isEditMode = false
                            taskTodo = ""
                        }) {
                            Text(text = "Save")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDialog = false
                            isEditMode = false
                            taskTodo = ""
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text(text = "Cancel")
                        }
                    },
                    title = { Text(text = "TodoList")},
                    text = {
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(
                            value = taskTodo,
                            onValueChange = {taskTodo = it},
                            label = {Text("Add Task")}
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                )
            }

    }




    }
}


@Composable
fun TodoCard(
    item:TodoListItem,
    editHandler:()->Unit,
    deleteHandler:()->Unit,
    completeHandler:(Boolean)->Unit,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.Gray),
        shape = RoundedCornerShape(10)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.task,
                )
            Row {
                IconButton(onClick = editHandler) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Button")
                }
                IconButton(onClick = deleteHandler ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Button" )
                }
                Checkbox(checked = item.isCompleted, onCheckedChange = { completeHandler(it) })
            }
        }
    }
}

@Composable
fun AssistChipContainer(todoSize:Int,todoComplete:Int){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
       AssistChip(
           onClick = { Log.d("Assist chip", "hello world") },
           label = { Text("Task : ${todoSize}") },
           leadingIcon = {
               Icon(
                   Icons.Filled.AccountBox,
                   contentDescription = "Localized description",
                   Modifier.size(AssistChipDefaults.IconSize)
               )
           }
       )
        AssistChip(
            onClick = { Log.d("Assist chip", "hello world") },
            label = { Text("Complete : ${todoComplete}") },
            leadingIcon = {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Localized description",
                    Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
        AssistChip(
            onClick = { Log.d("Assist chip", "hello world") },
            label = { Text("Progress : ${todoSize - todoComplete}") },
            leadingIcon = {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Localized description",
                    Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
    }
}

@Preview(showBackground = true)

@Composable
fun TodoPreview(){
    TodoList()
}