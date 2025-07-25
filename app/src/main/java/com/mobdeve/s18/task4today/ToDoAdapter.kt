package com.mobdeve.s18.task4today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter (
    private var activity: MainActivity,
    private var todoList : ArrayList<ToDoModel>,
    private var db : DbHelper
) : RecyclerView.Adapter<MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MyViewHolder {
        // 1. Create a LayoutInflater
        val inflater = LayoutInflater.from(parent.context)

        // 2. Inflate a new view
        val view = inflater.inflate(R.layout.task_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        // db.openDatabase() // don't call this na
        val item = todoList[position]

        // 3. Bind to-do data to the holder
        holder.task.text = item.task

        // Temporarily remove the listener before setting the checkbox
        holder.task.setOnCheckedChangeListener(null) // Remove previous listener
        holder.task.isChecked = toBoolean(item.status) // Set checkbox

        holder.task.setOnCheckedChangeListener{ _, isChecked ->
            val status = if(isChecked) 1 else 0
            db.updateStatus(item.id, status)
        }

    }

    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }

    override fun getItemCount() : Int {
        // return the number of to-do items
        return todoList.size
    }

    // Set tasks on the screen
    fun setTasks(todoList: ArrayList<ToDoModel>){
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun editItem(position: Int){
        val item = todoList[position]
        val bundle = Bundle().apply{
            putInt("id", item.id)
            putString("task", item.task)
        }

        val fragment = AddNewTask.newInstance(item.id, item.task)
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

}