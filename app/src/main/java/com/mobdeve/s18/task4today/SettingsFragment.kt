package com.mobdeve.s18.task4today

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.mobdeve.s18.task4today.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PREFS_NAME = "ThemePreferences"
        private const val DARK_MODE_KEY = "DarkModeEnabled"

        fun isDarkModeEnabled(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(DARK_MODE_KEY, false)
        }

        fun applySavedTheme(context: Context) {
            val isDarkMode = isDarkModeEnabled(context)
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button to navigate to another activity
        binding.editGroupButton.setOnClickListener {
            val intent = Intent(requireContext(), HeaderGroupsList::class.java)
            startActivity(intent)
        }

        // Set initial button text based on current theme
        updateThemeButtonText()

        // Toggle dark/light mode
        binding.themeButton.setOnClickListener {
            val sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isDarkMode = !isDarkModeEnabled(requireContext())
            sharedPrefs.edit().putBoolean(DARK_MODE_KEY, isDarkMode).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            requireActivity().recreate() // Restart to apply theme
        }
    }

    private fun updateThemeButtonText() {
        val isDarkMode = isDarkModeEnabled(requireContext())
        binding.themeButton.text = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
