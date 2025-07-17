package com.mobdeve.s18.task4today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.task4today.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TaskListAdapter  // Define adapter at the class level
    private lateinit var dbHelper: TaskDatabaseHelper // Declare dbHelper at class level

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = TaskDatabaseHelper(requireContext()) // Initialize dbHelper

        // Initial population of sample data if the database is empty
        if (dbHelper.isEmpty()) {
            populateSampleData()
        }

        loadAndDisplayTasks()

        // For resetDbButton
        // binding.root.findViewById<Button>(R.id.resetDbButton)?.setOnClickListener {
        //     resetAndRepopulateDatabase()
        // }

        binding.prevDayBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Previous day", Toast.LENGTH_SHORT).show()
        }

        binding.nextDayBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Next day", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateSampleData() {
        val mobicomId = dbHelper.insertHeader("MOBICOM", "#FF5722")
        dbHelper.insertTask(mobicomId, "Complete report", "9:00 AM", false)
        dbHelper.insertTask(mobicomId, "Submit summary", "11:00 AM", true)

        val itsecId = dbHelper.insertHeader("ITSECWB", "#3F51B5")
        dbHelper.insertTask(itsecId, "Review logs", "2:00 PM", false)
        Toast.makeText(requireContext(), "Sample data populated!", Toast.LENGTH_SHORT).show()
    }

    private fun loadAndDisplayTasks() {
        // Load from DB
        val data = dbHelper.getAllItems().toMutableList() // Convert to mutable list

        // Initialize the adapter if not already initialized, or update its data
        if (!::adapter.isInitialized) {
            adapter = TaskListAdapter(data) { headerId ->
                showAddTaskDialog(headerId, dbHelper)
            }
            binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.taskRecyclerView.adapter = adapter
        } else {
            adapter.updateData(data)
        }
    }

    private fun showAddTaskDialog(headerId: Long, dbHelper: TaskDatabaseHelper) {
        // Inflate and show the overlay dialog
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.overlay_add_task, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val taskNameInput = dialogView.findViewById<EditText>(R.id.taskNameInput)
        val hourInput = dialogView.findViewById<EditText>(R.id.hourInput)
        val minutesInput = dialogView.findViewById<EditText>(R.id.minutesInput)
        val amButton = dialogView.findViewById<android.widget.ToggleButton>(R.id.amButton)
        val pmButton = dialogView.findViewById<android.widget.ToggleButton>(R.id.pmButton)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        // Initialize AM/PM buttons with highlighting
        amButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                pmButton.isChecked = false
                amButton.setBackgroundColor(resources.getColor(R.color.toggle_checked)) // Highlight selected
                pmButton.setBackgroundColor(resources.getColor(R.color.toggle_unchecked)) // Reset other button
            }
        }

        pmButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amButton.isChecked = false
                pmButton.setBackgroundColor(resources.getColor(R.color.toggle_checked)) // Highlight selected
                amButton.setBackgroundColor(resources.getColor(R.color.toggle_unchecked)) // Reset other button
            }
        }

        // Confirm button functionality
        confirmButton.setOnClickListener {
            val name = taskNameInput.text.toString().trim()
            val hour = hourInput.text.toString().trim().padStart(2, '0')
            val minutes = minutesInput.text.toString().trim().padStart(2, '0')
            val ampm = if (amButton.isChecked) "AM" else "PM"

            // Validate the minutes input (should be between 0 and 59)
            val minutesInt = minutes.toIntOrNull()
            if (minutesInt == null || minutesInt !in 0..59) {
                Toast.makeText(requireContext(), "Please enter a valid minute between 00 and 59", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val time = "$hour:$minutes $ampm" // Updated to include minutes

            if (name.isNotEmpty() && hour.isNotEmpty() && minutes.isNotEmpty() && (amButton.isChecked || pmButton.isChecked)) {
                // Save the task to the correct header group in the database
                dbHelper.insertTask(headerId, name, time, false)

                Toast.makeText(requireContext(), "Task added: $name at $time", Toast.LENGTH_SHORT).show()

                // Reload tasks in RecyclerView under the correct header
                val updatedData = dbHelper.getAllItems()
                adapter.updateData(updatedData)  // Update the data in the adapter

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please complete all fields and select AM/PM", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel button functionality-
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}