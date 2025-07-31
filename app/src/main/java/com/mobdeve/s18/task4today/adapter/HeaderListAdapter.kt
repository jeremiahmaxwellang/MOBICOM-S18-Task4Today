// HeaderListAdapter.kt
package com.mobdeve.s18.task4today.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.HeaderModel
import com.mobdeve.s18.task4today.R
import com.mobdeve.s18.task4today.adapter.OnHeaderActionListener

class HeaderListAdapter(
    private val headerList: List<HeaderModel>, // List of HeaderModel objects
    private val itemLayout: Int,
    private val listener: OnHeaderActionListener
) : RecyclerView.Adapter<HeaderListAdapter.HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val header = headerList[position]

        // Set the title of the header
        holder.headerTitle.text = header.title

        // Ensure the color has a '#' symbol before passing to Color.parseColor()
        val color = if (header.color.startsWith("#")) header.color else "#${header.color}"

        // Set the background color of the headerContainer dynamically based on the header's color
        holder.headerContainer.setBackgroundColor(Color.parseColor(color))

        // Handle clicks to add tasks (if applicable)
        holder.itemView.setOnClickListener {
            listener.onAddTaskClicked(header)
        }
    }

    override fun getItemCount(): Int {
        return headerList.size
    }

    // ViewHolder to hold references to views
    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTitle: TextView = itemView.findViewById(R.id.headerTitle)
        val headerContainer: 'LinearLayout = itemView.findViewById(R.id.headerContainer) // Reference to headerContainer
    }

}

interface OnHeaderActionListener{
    // interface requires calling onAddTaskClicked
    fun onAddTaskClicked(header: HeaderModel)
}