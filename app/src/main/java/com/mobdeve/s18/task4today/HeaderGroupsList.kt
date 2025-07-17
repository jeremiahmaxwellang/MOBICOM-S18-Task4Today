package com.mobdeve.s18.task4today

import android.view.View
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class HeaderGroupsList : AppCompatActivity() {

    private lateinit var addBtn: ImageButton
    private lateinit var overlayAddHeader: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.header_task_groups_list)

        val addBtn = findViewById<ImageButton>(R.id.addBtn)
        val overlayAddHeader = findViewById<View>(R.id.overlayAddHeader)

        addBtn.setOnClickListener {
            overlayAddHeader.visibility = View.VISIBLE
        }

        // Optional: Dismiss overlay when clicked
        overlayAddHeader.setOnClickListener {
            overlayAddHeader.visibility = View.GONE
        }
    }
}