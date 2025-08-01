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
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.task4today.DbHelper
import com.mobdeve.s18.task4today.TaskModel

// HeaderListAdapter.kt
// Adapter for the Parent Recycler View for Header
class HeaderListAdapter(
    private val headers: List<HeaderModel>,
    private val layoutResId: Int, // Parameter to allow dynamic layout
    private var dbHelper : DbHelper,
    private val listener: OnHeaderActionListener // REQUIRED ADD BTN Action listener
) : RecyclerView.Adapter<HeaderListAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.headerTitle)
        val addButton: ImageView? = itemView.findViewById(R.id.headerAddButton) // Safe optional
        val taskRecyclerView: RecyclerView? = itemView.findViewById(R.id.taskListRecyclerView)

        fun bind(header: HeaderModel) {
            titleText.text = header.title
            addButton?.setOnClickListener {
                listener.onAddTaskClicked(header)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false) // The layout is now dynamic
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val header = headers[position]  // Get the header for this position
        holder.bind(header)

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

        // Set up RecyclerView for nested tasks
        holder.taskRecyclerView?.setHasFixedSize(true)
        holder.taskRecyclerView?.layoutManager = LinearLayoutManager(holder.itemView.context)
        val adapter = TaskAdapter(header.taskList, dbHelper)
        holder.taskRecyclerView?.adapter = adapter
    }

    override fun getItemCount(): Int = headers.size
}

interface OnHeaderActionListener {
    // Interface requires calling onAddTaskClicked
    fun onAddTaskClicked(header: HeaderModel)
}