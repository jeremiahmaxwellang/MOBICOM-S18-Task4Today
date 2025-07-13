package com.mobdeve.s18.task4today

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskListAdapter(private val items: List<ListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            is ListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ListItem.Task -> (holder as TaskViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.headerTitle)
        private val addButton: ImageView = itemView.findViewById(R.id.headerAddButton)

        fun bind(header: ListItem.Header) {
            title.text = header.title
            addButton.setOnClickListener {
                // TODO: Handle add task under this header
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

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Optional: update state, call callback, etc.
                // Example: Toast.makeText(itemView.context, "${task.title} is ${if (isChecked) "done" else "not done"}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
