package com.mobdeve.s18.task4today

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobdeve.s18.task4today.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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
                // Task Icon
                R.id.nav_tasks -> {
                    // Update icon for tasks tab to ic_tasks_clicked
                    binding.bottomNavigation.menu.findItem(R.id.nav_tasks).setIcon(R.drawable.ic_tasks_clicked)
                    replaceFragment(TaskListFragment())
                }

                // Calendar Icon
                R.id.nav_calendar -> {
                    // Update icon for calendar tab to ic_calendar_clicked
                    binding.bottomNavigation.menu.findItem(R.id.nav_calendar).setIcon(R.drawable.ic_calendar_clicked)
                    replaceFragment(CalendarFragment())
                }

                // Settings Icon
                R.id.nav_blank -> {
                    // Update icon for settings tab to ic_blank_clicked
                    binding.bottomNavigation.menu.findItem(R.id.nav_blank).setIcon(R.drawable.ic_blank_clicked)
                    replaceFragment(SettingsFragment())
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Helper function to reset all icons to their default state
    private fun resetIcons() {
        binding.bottomNavigation.menu.findItem(R.id.nav_tasks).setIcon(R.drawable.ic_tasks)
        binding.bottomNavigation.menu.findItem(R.id.nav_calendar).setIcon(R.drawable.ic_calendar)
        binding.bottomNavigation.menu.findItem(R.id.nav_blank).setIcon(R.drawable.ic_blank)
    }
}
