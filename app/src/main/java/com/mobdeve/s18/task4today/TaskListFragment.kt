package com.mobdeve.s18.task4today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.task4today.adapter.ToDoAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.addCallback
import com.mobdeve.s18.task4today.adapter.HeaderListAdapter
import com.mobdeve.s18.task4today.adapter.OnHeaderActionListener
import com.mobdeve.s18.task4today.databinding.FragmentTaskListBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HeaderListAdapter  // Define adapter at the class level
    private lateinit var dbHelper: DbHelper // Declare dbHelper at class level
    private lateinit var taskRecyclerView: RecyclerView // Declare taskRecyclerView at class level
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
        taskRecyclerView = binding.taskRecyclerView
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 2. Fetch headers from database
        dbHelper = DbHelper(requireContext())
        val headerList = dbHelper.getAllHeaders()

        // 3. Set up adapter using format_task_list_header.xml
        adapter = HeaderListAdapter(headerList, R.layout.format_task_list_header,
            object : OnHeaderActionListener {
                // Open ADD TASK Overlay
                override fun onAddTaskClicked(header: HeaderModel) {
                    val newTaskOverlay: View = binding.root.findViewById(R.id.overlayNewTask)
                    newTaskOverlay.visibility = View.VISIBLE

                    // Close overlay when clicking outside the modal
                    newTaskOverlay.setOnClickListener {
                        // Ignore click if the user clicks inside the modal container
                        val modalContainer: View = binding.root.findViewById(R.id.modalContainer)
                        if (it != modalContainer) {
                            newTaskOverlay.visibility = View.GONE
                        }
                    }

                    // Close overlay when clicking the cancel button
                    val cancelButton: Button = binding.root.findViewById(R.id.cancelButton)
                    cancelButton.setOnClickListener {
                        newTaskOverlay.visibility = View.GONE
                    }
                }
            })
        taskRecyclerView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Handle back button press inside the fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val newTaskOverlay: View = binding.root.findViewById(R.id.overlayNewTask)
            if (newTaskOverlay.visibility == View.VISIBLE) {
                newTaskOverlay.visibility = View.GONE
            } else {
                requireActivity().onBackPressed()  // Default back button behavior
            }
        }
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