package com.mobdeve.s18.task4today

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder(view : View) : RecyclerView.ViewHolder(view){
    // Checkbox
    val task : CheckBox = view.findViewById(R.id.taskCheckBox)

}