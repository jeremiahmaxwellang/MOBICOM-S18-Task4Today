package com.mobdeve.s18.task4today

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s18.task4today.AddNewTask.DialogCloseListener

class MainActivity : AppCompatActivity(), DialogCloseListener {
    private lateinit var tasksRecyclerView : RecyclerView
    private lateinit var adapter: ToDoAdapter
    private lateinit var addBtn : FloatingActionButton

    // Helper for swiping to edit/delete tasks
    private lateinit var itemTouchHelper : ItemTouchHelper

    // List of tasks
    private lateinit var taskList : ArrayList<TaskModel>

    private var dbHelper = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize to avoid error
        taskList = ArrayList()

        supportActionBar?.hide()

        // 1. Initialize RecyclerView
        this.tasksRecyclerView = findViewById(R.id.tasksRecyclerView)

        // 2. Set Adapter
        adapter = ToDoAdapter(this, taskList, dbHelper)
        this.tasksRecyclerView.adapter = adapter

        // 3. Set the layout manager
        this.tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        // 4. Add Tasks Floating Action Button (+)
        addBtn = findViewById(R.id.addBtn)
        // TODO: Fix addBtn OnClickListener
        addBtn.setOnClickListener{
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

        // 5. Set up helper for swiping tasks
        itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(adapter))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

        taskList = dbHelper.getAllTasks()
        taskList = ArrayList<TaskModel>(taskList.asReversed())
        adapter.setTasks(taskList)

    }

    // Close "Delete Task?" dialog box
    override fun handleDialogClose(dialog: DialogInterface) {
        taskList = dbHelper.getAllTasks()
        taskList = ArrayList<TaskModel>(taskList.asReversed())
        adapter.setTasks(taskList)
        adapter.notifyDataSetChanged()

    }
}
