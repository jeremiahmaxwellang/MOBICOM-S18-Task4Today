package com.mobdeve.s18.task4today

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var tasksRecyclerView : RecyclerView

    // List of tasks
    private lateinit var taskList : ArrayList<ToDoModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize taskList to avoid error
        taskList = ArrayList()

        supportActionBar?.hide()

        // 1. Initialize RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)

        // 2. Set Adapter
        setData(this.taskList) //temp for setting data
        tasksRecyclerView.adapter = ToDoAdapter(taskList)

        // 3. Set the layout manager
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    // temp function for setting data
    fun setData(taskList : ArrayList<ToDoModel>) {
        // Dummy data
        var task = ToDoModel(1, 0, "This is a test task")
        taskList.add(task)

        task = ToDoModel(2, 0, "This is a test task")
        taskList.add(task)

        task = ToDoModel(3, 0, "This is a test task")
        taskList.add(task)

        task = ToDoModel(4, 0, "This is a test task")
        taskList.add(task)

        task = ToDoModel(5, 1, "This is a test task")
        taskList.add(task)
    }
}
