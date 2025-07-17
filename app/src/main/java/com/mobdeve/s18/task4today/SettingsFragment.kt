package com.mobdeve.s18.task4today

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobdeve.s18.task4today.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editGroupButton.setOnClickListener {
            // 🔄 Navigate to HeaderGroupsList activity
            val intent = Intent(requireContext(), HeaderGroupsList::class.java)
            startActivity(intent)
        }

        binding.themeButton.setOnClickListener {
            // Existing code for theme button
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}