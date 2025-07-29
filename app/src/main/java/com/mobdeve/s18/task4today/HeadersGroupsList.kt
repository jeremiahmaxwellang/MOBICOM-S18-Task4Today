package com.mobdeve.s18.task4today

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s18.task4today.adapter.HeaderListAdapter
import com.mobdeve.s18.task4today.TaskHeader_ColorOption.TaskHeaderColorOption
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HeaderGroupsList : AppCompatActivity() {

    private lateinit var addBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var overlayAddHeader: View
    private lateinit var taskNameInput: EditText
    private lateinit var colorSpinner: Spinner
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private lateinit var completeTaskHeaderList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.header_task_groups_list)

        //Initialize views
        completeTaskHeaderList = findViewById(R.id.completeTaskHeaderList)
        completeTaskHeaderList.layoutManager = LinearLayoutManager(this)

        val dbHelper = DbHelper(this)
        val headerList = dbHelper.getAllHeaders()
        val adapter = HeaderListAdapter(headerList, R.layout.format_view_header_list)
        completeTaskHeaderList.adapter = HeaderListAdapter(headerList, R.layout.format_view_header_list)

        // Find UI components
        addBtn = findViewById(R.id.addBtn)
        backBtn = findViewById(R.id.backBtn)
        overlayAddHeader = findViewById(R.id.overlayAddHeader)
        taskNameInput = overlayAddHeader.findViewById(R.id.taskNameInput)
        colorSpinner = overlayAddHeader.findViewById(R.id.colorSpinner)
        confirmButton = overlayAddHeader.findViewById(R.id.confirmButton)
        cancelButton = overlayAddHeader.findViewById(R.id.cancelButton)

        // Combine color names and hex values into ColorOption list
        val colorNames = resources.getStringArray(R.array.header_color_names)
        val colorHexes = resources.getStringArray(R.array.header_colors)
        val colorOptions = colorNames.zip(colorHexes) { name, hex -> TaskHeaderColorOption(name, hex) }

        // Set up custom spinner adapter
        val colorAdapter = ColorSpinnerAdapter(this, colorOptions)
        colorSpinner.adapter = colorAdapter

        // Show overlay when Add button is clicked
        addBtn.setOnClickListener {
            overlayAddHeader.visibility = View.VISIBLE
        }

        // Cancel overlay
        cancelButton.setOnClickListener {
            overlayAddHeader.visibility = View.GONE
        }

        // Confirm header creation
        confirmButton.setOnClickListener {
            val headerTitle = taskNameInput.text.toString().trim()
            val selectedColorOption = colorSpinner.selectedItem as TaskHeaderColorOption

            if (headerTitle.isNotEmpty()) {
                val dbHelper = DbHelper(this)
                val header = HeaderModel(
                    id = 0,
                    title = headerTitle,
                    color = selectedColorOption.hex,
                    taskList = arrayListOf()
                )
                dbHelper.insertHeaders(header)
                Toast.makeText(this, "Task header added", Toast.LENGTH_SHORT).show()

                // Refresh the list
                val updatedHeaders = dbHelper.getAllHeaders()
                completeTaskHeaderList.adapter = HeaderListAdapter(updatedHeaders, R.layout.format_view_header_list)

                val updatedTitles = updatedHeaders.map { it.title }

                overlayAddHeader.visibility = View.GONE
                taskNameInput.text.clear()
            } else {
                Toast.makeText(this, "Please enter a header name", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle back button click
        backBtn.setOnClickListener {
            finish() // Return to SettingsFragment
        }
    }
}