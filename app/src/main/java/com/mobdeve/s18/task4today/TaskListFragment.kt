package com.mobdeve.s18.task4today

import android.app.TimePickerDialog
import android.view.ContextThemeWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.adapter.HeaderListAdapter
import com.mobdeve.s18.task4today.adapter.OnHeaderActionListener
import com.mobdeve.s18.task4today.databinding.FragmentTaskListBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragment : Fragment() {

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

        // Set dateLabel to date today
        val dateLabel = _binding?.dateLabel
        dateLabel?.setText(currentDate)

        // Previous Day Button
        _binding?.prevDayBtn?.setOnClickListener({
            val prevDate = getPreviousDate(currentDate)
            updateDateLabel(prevDate)
        })

        // Next Day Button
        _binding?.nextDayBtn?.setOnClickListener({
            val nextDate = getNextDate(currentDate)
            updateDateLabel(nextDate)
        })


        // Initialize RecyclerView
        headerRecyclerView = binding.headerRecyclerView
        headerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch headers from database
        dbHelper = DbHelper(requireContext())
        val headerList = dbHelper.getAllHeaders()

        // Set up adapter
        headerListAdapter = HeaderListAdapter(headerList, R.layout.format_task_list_header, dbHelper,
            object : OnHeaderActionListener {
                override fun onAddTaskClicked(header: HeaderModel) {
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
                            headerListAdapter.refreshTasksForHeader(header.id)
                            newTaskOverlay.visibility = View.GONE
                        }
                    }

                    // Cancel Button: Close overlay
                    cancelButton.setOnClickListener {
                        newTaskOverlay.visibility = View.GONE
                    }
                }
            })

        headerRecyclerView.adapter = headerListAdapter
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
        binding.dateLabel.text = date
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
