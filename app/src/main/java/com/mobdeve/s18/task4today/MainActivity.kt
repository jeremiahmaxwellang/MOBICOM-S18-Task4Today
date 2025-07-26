package com.mobdeve.s18.task4today

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s18.task4today.AddNewTask.DialogCloseListener

class MainActivity : AppCompatActivity(), DialogCloseListener {
    private lateinit var tasksRecyclerView : RecyclerView
    private lateinit var adapter: ToDoAdapter
    private lateinit var addBtn : FloatingActionButton

    // List of tasks
    private lateinit var taskList : ArrayList<ToDoModel>

    private var db = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize taskList to avoid error
        taskList = ArrayList()

        supportActionBar?.hide()

        // 1. Initialize RecyclerView
        this.tasksRecyclerView = findViewById(R.id.tasksRecyclerView)

        // 2. Set Adapter
//        setData(this.taskList) //temp for setting data
        adapter = ToDoAdapter(this, taskList, db)
        this.tasksRecyclerView.adapter = adapter

        // 3. Set the layout manager
        this.tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        // Floating Action Button (+)
        addBtn = findViewById(R.id.addBtn)
        // TODO: Fix addBtn OnClickListener
        addBtn.setOnClickListener{
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

        taskList = db.getAllTasks()
        taskList = ArrayList<ToDoModel>(taskList.asReversed())
        adapter.setTasks(taskList)

    }

    // temp function for setting data
//    fun setData(taskList : ArrayList<ToDoModel>) {
//        // Dummy data
//        var task = ToDoModel(1, 0, "This is a test task")
//        taskList.add(task)
//
//        task = ToDoModel(2, 0, "work on Essay 1")
//        taskList.add(task)
//
//        task = ToDoModel(3, 0, "watch sqlite tutorial")
//        taskList.add(task)
//
//        task = ToDoModel(4, 0, "change diaper")
//        taskList.add(task)
//
//        task = ToDoModel(5, 1, "make a steak")
//        taskList.add(task)
//    }

    // Close "Delete Task?" dialog box
    override fun handleDialogClose(dialog: DialogInterface) {
        taskList = db.getAllTasks()
        taskList = ArrayList<ToDoModel>(taskList.asReversed())
        adapter.setTasks(taskList)
        adapter.notifyDataSetChanged()

    }
}
