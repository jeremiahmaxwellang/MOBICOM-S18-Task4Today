package com.mobdeve.s18.task4today

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobdeve.s18.task4today.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔄 Apply saved theme BEFORE anything else
        applySavedTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment
        replaceFragment(TaskListFragment())

        // Set bottom nav listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // Reset all icons to default
            resetIcons()

            when (item.itemId) {
                R.id.nav_tasks -> {
                    binding.bottomNavigation.menu.findItem(R.id.nav_tasks)
                        .setIcon(R.drawable.ic_tasks_clicked)
                    replaceFragment(TaskListFragment())
                }
                R.id.nav_calendar -> {
                    binding.bottomNavigation.menu.findItem(R.id.nav_calendar)
                        .setIcon(R.drawable.ic_calendar_clicked)
                    replaceFragment(CalendarFragment())
                }
                R.id.nav_blank -> {
                    binding.bottomNavigation.menu.findItem(R.id.nav_blank)
                        .setIcon(R.drawable.ic_blank_clicked)
                    replaceFragment(SettingsFragment())
                }
            }
            true
        }
    }

    // 🔧 Applies the saved dark/light mode preference
    private fun applySavedTheme() {
        val sharedPrefs = getSharedPreferences("ThemePreferences", MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("DarkModeEnabled", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun resetIcons() {
        binding.bottomNavigation.menu.findItem(R.id.nav_tasks).setIcon(R.drawable.ic_tasks)
        binding.bottomNavigation.menu.findItem(R.id.nav_calendar).setIcon(R.drawable.ic_calendar)
        binding.bottomNavigation.menu.findItem(R.id.nav_blank).setIcon(R.drawable.ic_blank)
    }
}
