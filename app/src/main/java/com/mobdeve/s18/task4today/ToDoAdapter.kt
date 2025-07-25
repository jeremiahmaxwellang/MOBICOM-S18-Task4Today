package com.mobdeve.s18.task4today

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter (
    private var todoList : ArrayList<ToDoModel>
) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MyViewHolder {
        // 1. Create a LayoutInflater
        val inflater = LayoutInflater.from(parent.context)

        // 2. Inflate a new view
        val view = inflater.inflate(R.layout.task_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        // 3. Bind to-do data to the holder
        val item : ToDoModel = todoList.get(position)
        holder.task.setText(item.task)
        // set checkBoxes
        holder.task.setChecked(toBoolean(item.status))
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


}