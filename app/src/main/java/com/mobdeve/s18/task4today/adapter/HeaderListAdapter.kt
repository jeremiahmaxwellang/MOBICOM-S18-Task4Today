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
import com.mobdeve.s18.task4today.TaskModel

// HeaderListAdapter.kt
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


        }
    }

    override fun getItemCount(): Int = headers.size
}

interface OnHeaderActionListener{
    // interface requires calling onAddTaskClicked
    fun onAddTaskClicked(header: HeaderModel)
}