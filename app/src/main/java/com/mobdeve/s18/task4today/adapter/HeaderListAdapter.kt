package com.mobdeve.s18.task4today.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.HeaderModel
import com.mobdeve.s18.task4today.R
import android.graphics.Color
import androidx.core.graphics.drawable.DrawableCompat

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