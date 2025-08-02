package com.mobdeve.s18.task4today

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

    private lateinit var headerListAdapter: HeaderListAdapter  // Define adapter at the class level
    private lateinit var dbHelper: DbHelper // Declare dbHelper at class level
    private lateinit var headerRecyclerView: RecyclerView // Declare taskRecyclerView at class level
//    private lateinit var itemTouchHelper : ItemTouchHelper // Helper for swiping to edit/delete tasks

    var currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize RecyclerView
        headerRecyclerView = binding.headerRecyclerView
        headerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 2. Fetch headers from database
        dbHelper = DbHelper(requireContext())
        val headerList = dbHelper.getAllHeaders()

        // 3. Set up Header adapter using format_task_list_header.xml
        headerListAdapter = HeaderListAdapter(headerList, R.layout.format_task_list_header, dbHelper,
            object : OnHeaderActionListener {
                val newTaskOverlay: View = binding.root.findViewById(R.id.overlayNewTask)


                // Open ADD TASK Overlay
                override fun onAddTaskClicked(header: HeaderModel) {
                    newTaskOverlay.visibility = View.VISIBLE

                    // Close overlay when clicking outside the modal
                    newTaskOverlay.setOnClickListener {
                        // Ignore click if the user clicks inside the modal container
                        val modalContainer: View = binding.root.findViewById(R.id.modalContainer)
                        if(it != modalContainer) {
                            newTaskOverlay.visibility = View.GONE
                        }
                    }

                    // CONFIRM BUTTON: Add Task
                    val confirmButton: Button = binding.root.findViewById(R.id.confirmButton)
                    confirmButton.setOnClickListener {
                        val taskInput: EditText = newTaskOverlay.findViewById(R.id.taskInput)
                        var text = taskInput.text.toString()

                        // Insert new task if not empty
                        if (text.isNotEmpty()){
                            // DONE: Test if header.id passed is the correct one
                            // TODO: Fix date and time
                            val task = TaskModel(header.id, 0, text, currentDate, "12:00AM")
                            dbHelper.insertTasks(task)
                            newTaskOverlay.visibility = View.GONE

                            // TODO: Refresh Display
                            val updatedTasks = dbHelper.getAllTasks()
//                            header.taskList.clear()

                            // Refresh list Not working
                            val allTasks = dbHelper.getAllTasks() // list of ALL tasks
                            header.taskList = ArrayList(allTasks.filter{ it.header_id == id })
                            taskInput.text.clear()

                        }

                    }

                    // CANCEL BUTTON: Close overlay
                    val cancelButton: Button = binding.root.findViewById(R.id.cancelButton)
                    cancelButton.setOnClickListener {
                        newTaskOverlay.visibility = View.GONE
                    }
                }

            })
        headerRecyclerView.adapter = headerListAdapter

        // Set up helper for swiping tasks
//        itemTouchHelper = ItemTouchHelper(TaskItemTouchHelper(headerListAdapter.))
//        itemTouchHelper.attachToRecyclerView(headerRecyclerView)


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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}