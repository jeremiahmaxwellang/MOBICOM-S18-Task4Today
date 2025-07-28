package com.mobdeve.s18.task4today.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.HeaderModel
import com.mobdeve.s18.task4today.R

// HeaderListAdapter.kt
class HeaderListAdapter(private val headers: List<HeaderModel>) :
    RecyclerView.Adapter<HeaderListAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText = itemView.findViewById<TextView>(R.id.headerTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.format_view_header_list, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val header = headers[position]
        holder.titleText.text = header.title

        // Set the background color of the whole item view using the stored hex
        try {
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor(header.color))
        } catch (e: IllegalArgumentException) {
            holder.itemView.setBackgroundColor(android.graphics.Color.GRAY)
        }
    }

    override fun getItemCount(): Int = headers.size
}