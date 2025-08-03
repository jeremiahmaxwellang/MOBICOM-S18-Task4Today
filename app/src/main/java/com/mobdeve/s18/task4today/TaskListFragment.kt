package com.mobdeve.s18.task4today
/*
    MOBICOM S18 Group 6
    Jeremiah Ang
    Charles Duelas
    Justin Lee
 */

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.adapter.HeaderListAdapter
import com.mobdeve.s18.task4today.adapter.OnHeaderActionListener
import com.mobdeve.s18.task4today.databinding.FragmentTaskListBinding
import java.text.SimpleDateFormat
import java.util.*

// Task List Page (Default Page)
class TaskListFragment : Fragment(), EditTask.DialogCloseListener {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var headerRecyclerView: RecyclerView
    private lateinit var headerListAdapter: HeaderListAdapter

    private lateinit var dbHelper: DbHelper

    var currentDate: String = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        headerRecyclerView = binding.headerRecyclerView
        headerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch headers from database
        dbHelper = DbHelper(requireContext())
        val headerList = dbHelper.getAllHeaders(currentDate)

        // Set up adapter
        headerListAdapter = HeaderListAdapter(headerList, R.layout.format_task_list_header, dbHelper, requireActivity(), this,
            object : OnHeaderActionListener {
                override fun onAddTaskClicked(header: HeaderModel) {
                    setAddTaskListeners(header)
                }
            })

        headerRecyclerView.adapter = headerListAdapter

        // Set dateLabel to date today
        val dateLabel = _binding?.dateLabel
        dateLabel?.text = currentDate

        // Previous Day Button
        _binding?.prevDayBtn?.setOnClickListener {
            val prevDate = getPreviousDate(currentDate)
            // Updates the date label
            updateDateLabel(prevDate)

            updateHeaderAdapter(prevDate)
        }

        // Next Day Button
        _binding?.nextDayBtn?.setOnClickListener {
            val nextDate = getNextDate(currentDate)
            updateDateLabel(nextDate)

            updateHeaderAdapter(nextDate)
        }
    }

    // Function for getting the date yesterday
    private fun getPreviousDate(date: String): String {
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val currentDate = sdf.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return sdf.format(calendar.time)
    }

    // Function for updating the date label
    private fun updateDateLabel(date: String) {
        this.currentDate = date
        binding.dateLabel.text = this.currentDate
    }

    // Function for getting the date tomorrow
    private fun getNextDate(date: String): String {
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val currentDate = sdf.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return sdf.format(calendar.time)
    }

    fun setAddTaskListeners(header: HeaderModel) {
        val newTaskOverlay: View = binding.root.findViewById(R.id.overlayNewTask)
        newTaskOverlay.visibility = View.VISIBLE

        val modalContainer: View = newTaskOverlay.findViewById(R.id.modalContainer)
        val taskInput: EditText = newTaskOverlay.findViewById(R.id.taskInput)
        val timeButton: Button = newTaskOverlay.findViewById(R.id.timeButton)
        val confirmButton: Button = newTaskOverlay.findViewById(R.id.confirmButton)
        val cancelButton: Button = newTaskOverlay.findViewById(R.id.cancelButton)

        // Reset previous values
        taskInput.text.clear()
        timeButton.text = "Select Time"
        var selectedTime = "12:00AM"

        // Show Time Picker when clicking "Select Time"
        timeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                ContextThemeWrapper(requireContext(), R.style.TimePickerDialogSpinner), // <-- use custom style
                { _, selectedHour, selectedMinute ->
                    val amPmFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    calendar.set(Calendar.MINUTE, selectedMinute)
                    selectedTime = amPmFormat.format(calendar.time)
                    timeButton.text = selectedTime
                },
                hour, minute, false
            )

            timePickerDialog.show()
        }

        // Close overlay when clicking outside modal
        newTaskOverlay.setOnClickListener {
            if (it != modalContainer) {
                newTaskOverlay.visibility = View.GONE
            }
        }

        // Confirm Button: Save task
        confirmButton.setOnClickListener {
            val text = taskInput.text.toString()
            if (text.isNotEmpty()) {
                val task = TaskModel(header.id, 0, text, currentDate, selectedTime)
                dbHelper.insertTasks(task)
                // Pass currentDate when refreshing tasks
                headerListAdapter.refreshTasksForHeader(header.id, currentDate)
                newTaskOverlay.visibility = View.GONE
            }
        }

        // Cancel Button: Close overlay
        cancelButton.setOnClickListener {
            newTaskOverlay.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Refresh Header Adapter when returning from another activity
    override fun onResume() {
        super.onResume()
        updateHeaderAdapter(this.currentDate)
    }

    // Modify the updateHeaderAdapter function to remove tasks that don't match the current date
    fun updateHeaderAdapter(date: String) {
        // Log the current date tasks being loaded
        Log.d("TaskListFragment", "Loading tasks for date: $date")

        // Get new headers for the selected date
        val headerList = dbHelper.getAllHeaders(date)  // Fetch headers and tasks for the selected date

        // Remove tasks that don't match the current date and convert it to ArrayList
        headerList.forEach { header ->
            header.taskList = ArrayList(header.taskList.filter { task -> task.date == date })
        }

        // Log the tasks loaded for the current date
        headerList.forEach { header ->
            header.taskList.forEach { task ->
                Log.d("TaskListFragment", "Loaded Task: ${task.task} | Assigned Time: ${task.time} | Date: ${task.date}")
            }
        }

        // Create a new adapter with the updated list
        this.headerListAdapter = HeaderListAdapter(headerList, R.layout.format_task_list_header, dbHelper, requireActivity(), this, object : OnHeaderActionListener {
            override fun onAddTaskClicked(header: HeaderModel) {
                setAddTaskListeners(header)
            }
        })

        // Set the new adapter to the RecyclerView
        binding.headerRecyclerView.adapter = headerListAdapter

        // Log all tasks currently being displayed in the layout
        Log.d("TaskListFragment", "Currently displayed tasks for date: $date")
        headerList.forEach { header ->
            header.taskList.forEach { task ->
                Log.d("TaskListFragment", "Displayed Task: ${task.task} | Assigned Time: ${task.time} | Date: ${task.date}")
            }
        }

        // Refresh tasks for each header
        headerList.forEach { header ->
            headerListAdapter.refreshTasksForHeader(header.id, date) // Pass the current date
        }
    }


    // Edit Task Dialog Listener
    override fun handleDialogClose(dialog: DialogInterface) {
        // Refresh headers after update
        updateHeaderAdapter(this.currentDate)
        Toast.makeText(requireContext(), "Task edited successfully!", Toast.LENGTH_SHORT).show()
    }
}
