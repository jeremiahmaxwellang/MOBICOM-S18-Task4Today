package com.mobdeve.s18.task4today

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskListAdapter(
    private var items: MutableList<ListItem>,  // MutableList for easy updates
    private val showAddTaskDialog: (Long) -> Unit // Accepts headerId to show the dialog
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_TASK = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Header -> VIEW_TYPE_HEADER
            is ListItem.Task -> VIEW_TYPE_TASK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.fragment_task_list_header, parent, false)
                HeaderViewHolder(view)
            }
            VIEW_TYPE_TASK -> {
                val view = inflater.inflate(R.layout.fragment_task_list_item, parent, false)
                TaskViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderViewHolder).bind(item, showAddTaskDialog)
            is ListItem.Task -> (holder as TaskViewHolder).bind(item)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.headerTitle)
        private val addButton: ImageView = itemView.findViewById(R.id.headerAddButton)

        fun bind(header: ListItem.Header, showAddTaskDialog: (Long) -> Unit) {
            title.text = header.title
            addButton.setOnClickListener {
                showAddTaskDialog(header.headerId)
            }
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
        private val time: TextView = itemView.findViewById(R.id.taskTime)

        fun bind(task: ListItem.Task) {
            checkBox.text = task.title
            checkBox.isChecked = task.isChecked
            time.text = task.time
        }
    }

    override fun getItemCount(): Int = items.size

    // Update data in adapter
    fun updateData(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Add tasks to the existing header
    fun addTasks(newTasks: List<ListItem.Task>) {
        items.addAll(newTasks)
        notifyDataSetChanged()
    }
}