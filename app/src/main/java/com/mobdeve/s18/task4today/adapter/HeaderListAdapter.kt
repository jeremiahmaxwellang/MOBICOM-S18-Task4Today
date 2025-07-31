package com.mobdeve.s18.task4today.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.HeaderModel
import com.mobdeve.s18.task4today.R
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.mobdeve.s18.task4today.AddNewTask
import com.mobdeve.s18.task4today.DbHelper
import com.mobdeve.s18.task4today.MainActivity
import com.mobdeve.s18.task4today.MyViewHolder
import com.mobdeve.s18.task4today.TaskModel

// HeaderListAdapter.kt
// Adapter for the Parent Recycler View for Header
class HeaderListAdapter(
    private val headers: List<HeaderModel>,
    private val layoutResId: Int, // Parameter to allow dynamic layout
    private val listener: OnHeaderActionListener //REQUIRED ADD BTN Action listener
) :

RecyclerView.Adapter<HeaderListAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.headerTitle)
        val addButton: ImageView? = itemView.findViewById(R.id.headerAddButton) // Safe optional

        fun bind(header: HeaderModel){
            titleText.text = header.title
            addButton?.setOnClickListener {
                listener.onAddTaskClicked(header)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutResId, parent, false) // The layout is now dynamic

        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val header = headers[position]
//        holder.titleText.text = header.title
        holder.bind(header)

        // Tint the background drawable with the header's color
        val background = holder.itemView.background?.mutate()
        if (background != null) {
            try {
                val wrapped = DrawableCompat.wrap(background)
                DrawableCompat.setTint(wrapped, Color.parseColor(header.color))
                holder.itemView.background = wrapped
            } catch (e: IllegalArgumentException) {
                // Fallback tint
                DrawableCompat.setTint(background, Color.GRAY)
            }

//            // Show overlay when Add button is clicked
//            holder.addButton?.setOnClickListener {
//                holder.overlayNewTask.visibility = View.VISIBLE
//            }
//
//            // Cancel overlay
//            holder.cancelButton?.setOnClickListener {
//                holder.overlayNewTask.visibility = View.GONE
//            }

            // TODO: Confirm new task
//            holder.confirmButton.setOnClickListener {
//                val headerTitle = taskNameInput.text.toString().trim()
//                val selectedColorOption = colorSpinner.selectedItem as TaskHeaderColorOption
//
//                if (headerTitle.isNotEmpty()) {
//                    val dbHelper = DbHelper(this)
//                    val header = HeaderModel(
//                        id = 0,
//                        title = headerTitle,
//                        color = selectedColorOption.hex,
//                        taskList = arrayListOf()
//                    )
//                    dbHelper.insertHeaders(header)
//                    Toast.makeText(this, "Task header added", Toast.LENGTH_SHORT).show()
//
//                    // Refresh the list
//                    val updatedHeaders = dbHelper.getAllHeaders()
//                    completeTaskHeaderList.adapter = HeaderListAdapter(updatedHeaders, R.layout.format_view_header_list)
//
//                    val updatedTitles = updatedHeaders.map { it.title }
//
//                    overlayAddHeader.visibility = View.GONE
//                    taskNameInput.text.clear()
//                } else {
//                    Toast.makeText(this, "Please enter a header name", Toast.LENGTH_SHORT).show()
//                }
//            }

        }
    }

    override fun getItemCount(): Int = headers.size
}

class ToDoAdapter (
    val activity: MainActivity,
    private var todoList : ArrayList<TaskModel>,
    private var dbHelper : DbHelper
) : RecyclerView.Adapter<MyViewHolder>() {

    fun getContext(): Context {
        return activity
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MyViewHolder {
        // 1. Create a LayoutInflater
        val inflater = LayoutInflater.from(parent.context)

        // 2. Inflate a new view
        val view = inflater.inflate(R.layout.format_task_layout, parent, false)
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
            dbHelper.updateStatus(item.id, status)
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
    fun setTasks(todoList: ArrayList<TaskModel>){
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun editItem(position: Int){
        val item = todoList[position]
        val bundle = Bundle().apply{
            putInt("id", item.id)
            putString("task", item.task)
        }

        val fragment = AddNewTask.Companion.newInstance(item.id, item.task)
        fragment.show(activity.supportFragmentManager, AddNewTask.Companion.TAG)
    }

    // Delete task
    fun deleteItem(position: Int){
        val item = todoList[position]
        dbHelper.deleteTask(item.id)
        todoList.removeAt(position)
        notifyItemRemoved(position)

    }

}

interface OnHeaderActionListener{
    // interface requires calling onAddTaskClicked
    fun onAddTaskClicked(header: HeaderModel)
}