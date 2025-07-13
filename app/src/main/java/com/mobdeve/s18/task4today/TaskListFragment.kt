package com.mobdeve.s18.task4today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.addTaskButton.setOnClickListener {
            Toast.makeText(requireContext(), "Add Task clicked", Toast.LENGTH_SHORT).show()
        }

        binding.prevDayBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Previous day", Toast.LENGTH_SHORT).show()
        }

        binding.nextDayBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Next day", Toast.LENGTH_SHORT).show()
        }

        // 🟢 Setup sample data and adapter here
        val sampleData = listOf(
            ListItem.Header("MOBICOM"),
            ListItem.Task("Complete report", "9:00 AM", false),
            ListItem.Task("Submit summary", "11:00 AM", true),
            ListItem.Header("ITSECWB"),
            ListItem.Task("Review logs", "2:00 PM", false)
        )

        val adapter = TaskListAdapter(sampleData)
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
