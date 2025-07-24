package com.mobdeve.s18.task4today

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mobdeve.s18.task4today.TaskHeader_ColorOption.TaskHeaderColorOption

class ColorSpinnerAdapter(
    context: Context,
    private val colors: List<TaskHeaderColorOption>
) : ArrayAdapter<TaskHeaderColorOption>(context, 0, colors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_color_item, parent, false)

        val colorName = view.findViewById<TextView>(R.id.colorName)
        val colorPreview = view.findViewById<View>(R.id.colorPreview)

        val item = colors[position]
        colorName.text = item.name
        colorPreview.setBackgroundColor(Color.parseColor(item.hex)) // <- Error was here

        return view
    }
}