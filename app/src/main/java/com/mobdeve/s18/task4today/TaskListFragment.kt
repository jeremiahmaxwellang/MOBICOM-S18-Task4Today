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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = TaskDatabaseHelper(requireContext())

        // Insert sample data only if DB is empty
        if (dbHelper.isEmpty()) {
            val mobicomId = dbHelper.insertHeader("MOBICOM", "#FF5722")
            dbHelper.insertTask(mobicomId, "Complete report", "9:00 AM", false)
            dbHelper.insertTask(mobicomId, "Submit summary", "11:00 AM", true)

            val itsecId = dbHelper.insertHeader("ITSECWB", "#3F51B5")
            dbHelper.insertTask(itsecId, "Review logs", "2:00 PM", false)
        }

        // Load from DB
        val data = dbHelper.getAllItems()

        val adapter = TaskListAdapter(data) {
            showAddTaskDialog(dbHelper) // Pass the function to show the add task dialog
        }

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerView.adapter = adapter

        binding.prevDayBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Previous day", Toast.LENGTH_SHORT).show()
        }

        binding.nextDayBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Next day", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddTaskDialog(dbHelper: TaskDatabaseHelper) {
        // Inflate and show the overlay dialog
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.overlay_add_task, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val taskNameInput = dialogView.findViewById<EditText>(R.id.taskNameInput)
        val hourInput = dialogView.findViewById<EditText>(R.id.hourInput)
        val amButton = dialogView.findViewById<android.widget.ToggleButton>(R.id.amButton)
        val pmButton = dialogView.findViewById<android.widget.ToggleButton>(R.id.pmButton)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        // Make AM/PM mutually exclusive
        amButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) pmButton.isChecked = false
        }
        pmButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) amButton.isChecked = false
        }

        confirmButton.setOnClickListener {
            val name = taskNameInput.text.toString().trim()
            val hour = hourInput.text.toString().trim().padStart(2, '0')
            val ampm = if (amButton.isChecked) "AM" else "PM"
            val time = "$hour:00 $ampm"

            if (name.isNotEmpty() && hour.isNotEmpty()) {
                // Save to database
                val headerId = dbHelper.insertHeader("New Group", "#FF4081") // Example: "New Group"
                dbHelper.insertTask(headerId, name, time, false)

                Toast.makeText(requireContext(), "Task added: $name at $time", Toast.LENGTH_SHORT).show()

                (binding.taskRecyclerView.adapter as TaskListAdapter).notifyDataSetChanged()

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please complete all fields", Toast.LENGTH_SHORT).show()
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