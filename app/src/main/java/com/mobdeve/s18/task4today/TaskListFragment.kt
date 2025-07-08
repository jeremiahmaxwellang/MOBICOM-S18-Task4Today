package com.task4today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}