package com.mobdeve.s18.task4today.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.DbHelper
import com.mobdeve.s18.task4today.R
import com.mobdeve.s18.task4today.TaskModel

class TaskAdapter(
    private var taskList: ArrayList<TaskModel>,
    private var dbHelper : DbHelper,
    private var context: Context
): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        // Initialize view elements
        val taskCheckBox = itemView.findViewById<CheckBox>(R.id.taskCheckBox)
        val taskName = itemView.findViewById<TextView>(R.id.taskName)
        val taskTime = itemView.findViewById<TextView>(R.id.taskTime)
    }

    // Getter for taskList
    fun getTaskList(): ArrayList<TaskModel> {
        return taskList
    }

    // Setter for taskList
    fun setTaskList(newTaskList: ArrayList<TaskModel>) {
        this.taskList = newTaskList
        notifyDataSetChanged()  // Refresh the RecyclerView if needed
    }

    // Getter for TaskAdapter's context (current activity)
    fun getContext() : Context {
        return this.context
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

        // Set the checkbox tint dynamically based on the current theme
        val checkboxTint = if (isDarkModeEnabled(holder.itemView.context)) {
            ContextCompat.getColor(holder.itemView.context, R.color.checkbox_tint_dark) // Dark mode tint
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.checkbox_tint_light) // Light mode tint
        }

        // Apply the tint to the checkbox
        holder.taskCheckBox.buttonTintList = android.content.res.ColorStateList.valueOf(checkboxTint)

        holder.taskCheckBox.setOnCheckedChangeListener{ _, isChecked ->
            val status = if(isChecked) 1 else 0
            dbHelper.updateStatus(task.id, status)
        }

        // set xml elements
        holder.taskName.text = task.task
        holder.taskTime.text = task.time
    }

    // Function to check if dark mode is enabled
    private fun isDarkModeEnabled(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // Set tasks on the screen
    fun setTasks(taskList: ArrayList<TaskModel>) {
        this.taskList = taskList

        // Refresh layout after setting new tasks
        notifyDataSetChanged()
    }

    // TODO: Edit task (Either an "Edit task" overlay should pop up, or just directly edit the task text nalang)
    fun editTask(position: Int){
        val task = taskList[position]
        val bundle = Bundle().apply{
            putInt("id", task.id)
            putString("task", task.task) // Edit text inside task
        }

        setTasks(taskList) // Refresh displayed taskList
    }

    // Delete task
    fun deleteTask(position: Int) {
        // Check if position is valid (within bounds)
        if (position >= 0 && position < taskList.size) {
            val task = taskList[position]
            dbHelper.deleteTask(task.id) // remove from DB
            taskList.removeAt(position)
            notifyItemRemoved(position)
        } else {
            Log.e("TaskAdapter", "Invalid position: $position. Task list size is ${taskList.size}")
        }
    }

    // Converts Integers to Boolean values
    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }

    // Function to refresh the layout manually
    fun refreshLayout() {
        notifyDataSetChanged() // Refresh the entire RecyclerView layout
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