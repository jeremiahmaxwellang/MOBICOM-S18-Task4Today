package com.mobdeve.s18.task4today.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.DbHelper
import com.mobdeve.s18.task4today.R
import com.mobdeve.s18.task4today.TaskModel

class TaskAdapter(
    private var taskList: ArrayList<TaskModel>,
    private var dbHelper : DbHelper
): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        // Initialize view elements
        val taskCheckBox = itemView.findViewById<CheckBox>(R.id.taskCheckBox)
        val taskName = itemView.findViewById<TextView>(R.id.taskName)
        val taskTime = itemView.findViewById<TextView>(R.id.taskTime)
    }

    override fun onCreateViewHolder(header: ViewGroup, ViewType: Int) : TaskViewHolder {
        // Inflate task layout
        val view = LayoutInflater.from(header.context).inflate(R.layout.format_task_layout, header, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // Temporarily remove the listener before setting the checkbox
        holder.taskCheckBox.setOnCheckedChangeListener(null) // Remove previous listener
        holder.taskCheckBox.isChecked = toBoolean(task.status) // Set checkbox

        holder.taskCheckBox.setOnCheckedChangeListener{ _, isChecked ->
            val status = if(isChecked) 1 else 0
            dbHelper.updateStatus(task.id, status)
        }

        // set xml elements
        holder.taskName.text = task.task
        holder.taskTime.text = task.time
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // Set tasks on the screen
    fun setTasks(taskList: ArrayList<TaskModel>){
        this.taskList = taskList
        notifyDataSetChanged()
    }

    // TODO: editTask()
    // dbHelper.updateTask(bundle?.getInt("id") ?: -1, text)

    // TODO: deleteTask()

    // Converts Integers to Boolean values
    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }
}

// Sample code for editing / deleting tasks
//fun editItem(position: Int){
//    val item = todoList[position]
//    val bundle = Bundle().apply{
//        putInt("id", item.id)
//        putString("task", item.task)
//    }
//
//    val fragment = AddNewTask.Companion.newInstance(item.id, item.task)
//    fragment.show(activity.supportFragmentManager, AddNewTask.Companion.TAG)
//}
//
//// Delete task
//fun deleteItem(position: Int){
//    val item = todoList[position]
//    dbHelper.deleteTask(item.id)
//    todoList.removeAt(position)
//    notifyItemRemoved(position)
//
//}