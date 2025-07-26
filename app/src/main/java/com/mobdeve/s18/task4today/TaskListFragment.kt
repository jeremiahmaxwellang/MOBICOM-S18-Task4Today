package com.mobdeve.s18.task4today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mobdeve.s18.task4today.databinding.FragmentTaskListBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ToDoAdapter  // Define adapter at the class level
    private lateinit var dbHelper: DbHelper // Declare dbHelper at class level
    var currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getPreviousDate(date: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, -1)  // Go back one day
        return sdf.format(calendar.time)
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val formattedDate = sdf.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(currentDate)!!)
        binding.dateLabel.text = formattedDate
    }

    private fun getNextDate(date: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 1)  // Go forward one day
        return sdf.format(calendar.time)
    }

    private fun loadAndDisplayTasks() {
        // Ensure headers are loaded once
        if (adapter.itemCount == 0) {
            val headers = dbHelper.getAllItems().filterIsInstance<ListItem.Header>()
            adapter.updateData(headers)
        }

        // Load tasks for the current date
        val tasks = dbHelper.getTasksForDate(currentDate).filterIsInstance<ListItem.Task>()
        adapter.addTasks(tasks)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DbHelper(requireContext())

        // Initialize adapter
        adapter = ToDoAdapter(mutableListOf()) { headerId -> showAddTaskDialog(headerId, dbHelper) }
        binding.taskRecyclerView.adapter = adapter

        // If database is empty, populate with sample data
        if (dbHelper.isEmpty()) {
            populateSampleData()
        }

        loadAndDisplayTasks()
        updateDateLabel()

        binding.prevDayBtn.setOnClickListener {
            currentDate = getPreviousDate(currentDate)
            loadAndDisplayTasks()
            updateDateLabel()
            Toast.makeText(requireContext(), "Previous day: $currentDate", Toast.LENGTH_SHORT).show()
        }

        binding.nextDayBtn.setOnClickListener {
            currentDate = getNextDate(currentDate)
            loadAndDisplayTasks()
            updateDateLabel()
            Toast.makeText(requireContext(), "Next day: $currentDate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateSampleData() {
        val mobicomId = dbHelper.insertHeader("MOBICOM", "#FF5722")
        dbHelper.insertTask(mobicomId, "Complete report", "9:00 AM", false, "2025-07-16")

        val itsecId = dbHelper.insertHeader("ITSECWB", "#3F51B5")
        dbHelper.insertTask(itsecId, "Review logs", "2:00 PM", false, currentDate)

        Toast.makeText(requireContext(), "Sample data populated!", Toast.LENGTH_SHORT).show()
    }

    private fun showAddTaskDialog(headerId: Long, dbHelper: TaskDatabaseHelper) {
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
                amButton.setBackgroundColor(resources.getColor(R.color.toggle_checked))
                pmButton.setBackgroundColor(resources.getColor(R.color.toggle_unchecked))
            }
        }

        pmButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amButton.isChecked = false
                pmButton.setBackgroundColor(resources.getColor(R.color.toggle_checked))
                amButton.setBackgroundColor(resources.getColor(R.color.toggle_unchecked))
            }
        }

        confirmButton.setOnClickListener {
            val name = taskNameInput.text.toString().trim()
            val hour = hourInput.text.toString().trim().padStart(2, '0')
            val minutes = minutesInput.text.toString().trim().padStart(2, '0')
            val ampm = if (amButton.isChecked) "AM" else "PM"

            val minutesInt = minutes.toIntOrNull()
            if (minutesInt == null || minutesInt !in 0..59) {
                Toast.makeText(requireContext(), "Please enter a valid minute between 00 and 59", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val time = "$hour:$minutes $ampm"

            // Get current date (same date as in the dateLabel)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (name.isNotEmpty() && hour.isNotEmpty() && minutes.isNotEmpty() && (amButton.isChecked || pmButton.isChecked)) {
                dbHelper.insertTask(headerId, name, time, false, currentDate)  // Use currentDate

                Toast.makeText(requireContext(), "Task added: $name at $time", Toast.LENGTH_SHORT).show()

                val updatedData = dbHelper.getAllItems()
                adapter.updateData(updatedData)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please complete all fields and select AM/PM", Toast.LENGTH_SHORT).show()
            }
        }

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