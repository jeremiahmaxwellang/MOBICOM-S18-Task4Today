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
import android.graphics.drawable.ColorDrawable
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.task4today.DbHelper
import TaskItemTouchHelper
import android.util.Log

// HeaderListAdapter.kt
// Adapter for the Parent Recycler View for Header
class HeaderListAdapter(
    private val headers: ArrayList<HeaderModel>,
    private val layoutResId: Int, // Parameter to allow dynamic layout
    private var dbHelper : DbHelper,
    private val listener: OnHeaderActionListener? = null // REQUIRED ADD BTN Action listener
) : RecyclerView.Adapter<HeaderListAdapter.HeaderViewHolder>() {

    private lateinit var context: Context
    private val viewHolderMap = mutableMapOf<Int, HeaderViewHolder>() // Store ViewHolder here to access the child recyclerView
    private val taskAdapterMap = mutableMapOf<Int, TaskAdapter>() // store all the taskAdapters of each header's recyclerView

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.headerTitle)
        val addButton: ImageView? = itemView.findViewById(R.id.headerAddButton) // Safe optional
        val taskRecyclerView: RecyclerView? = itemView.findViewById(R.id.taskListRecyclerView) // Optional child recyclerView

        fun bind(header: HeaderModel) {
            titleText.text = header.title
            addButton?.setOnClickListener {
                listener?.onAddTaskClicked(header)
            }
        }
    }

    // Getter for HeaderListAdapter's context (current activity)
    fun getContext() : Context {
        return this.context
    }

    // Getter for header arrayList
    fun getHeaders() : ArrayList<HeaderModel> {
        return this.headers
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        this.context = parent.context // Set adapter's context to current activity

        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false) // The layout is now dynamic
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val header = headers[position]  // Get the header for this position
        holder.bind(header)
        viewHolderMap[position] = holder

        // Get the headerLayout for the current item
        val headerLayout = holder.itemView.findViewById<LinearLayout>(R.id.headerLayout)

        // Check if headerLayout is not null
        if (headerLayout != null) {
            try {
                // Get the color from the model
                val color = Color.parseColor(header.color)
                // Create a ColorDrawable with the color
                val colorDrawable = ColorDrawable(color)

                // Apply the rounded corners and dynamic color
                val background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.header_background)

                // Wrap the background drawable and apply the color dynamically
                background?.let {
                    val wrapped = DrawableCompat.wrap(it)
                    DrawableCompat.setTint(wrapped, color)  // Apply the dynamic color
                    headerLayout.background = wrapped  // Set the new background with both color and corners
                }

            } catch (e: IllegalArgumentException) {
                // Fallback to default color if invalid color format
                val fallbackBackground = ContextCompat.getDrawable(holder.itemView.context, R.drawable.header_background)
                headerLayout.background = fallbackBackground  // Use default background with rounded corners
            }
        }

        // Set up this Header's child recyclerView
        holder.taskRecyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(holder.itemView.context)

            val taskAdapter = TaskAdapter(header.taskList, dbHelper, this.context)
            adapter = taskAdapter // set adapter of taskRecyclerView
            taskAdapterMap[position] = taskAdapter // store all the taskAdapters of each header's taskRecyclerView


            // Set up helper for swiping tasks
            val itemTouchHelper = ItemTouchHelper(TaskItemTouchHelper(taskAdapter))
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    override fun getItemCount(): Int = headers.size

    // Refresh this header's tasks after Add/Update
    fun refreshTasksForHeader(headerId: Int, currentDate: String) {
        val position = headers.indexOfFirst { it.id == headerId }
        if (position != -1) {
            // Get tasks for the specified header and current date
            val updatedTasks = dbHelper.getAllTasks(currentDate).filter { it.header_id == headerId }

            // Update the task list for the header
            headers[position].taskList = ArrayList(updatedTasks)

            // Store all taskAdapters of every header
            taskAdapterMap[position]?.let { adapter ->
                adapter.setTasks(ArrayList(updatedTasks))
                notifyItemChanged(position)
            }

            // Ensure the item view is updated
            notifyItemChanged(position)
        }
    }

    // Delete Task Group Headers
    fun deleteTaskGroup(position: Int) {
        // Check if position is valid (within bounds)
        if (position >= 0 && position < headers.size) {
            val header = headers[position]
            dbHelper.deleteHeader(header.id) // remove from DB
            headers.removeAt(position)
            notifyItemRemoved(position)
        } else {
            Log.e("HeaderListAdapter", "Invalid position: $position. Header list size is ${headers.size}")
        }
    }

    // Function to refresh the layout manually
    fun refreshLayout() {
        notifyDataSetChanged() // Refresh the entire RecyclerView layout
    }
}

interface OnHeaderActionListener {
    // Interface requires calling onAddTaskClicked
    fun onAddTaskClicked(header: HeaderModel)
}