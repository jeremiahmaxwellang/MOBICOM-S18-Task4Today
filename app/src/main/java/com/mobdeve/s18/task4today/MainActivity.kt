package com.mobdeve.s18.task4today

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import com.mobdeve.s18.task4today.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔄 Apply saved theme BEFORE anything else
        applySavedTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment (TaskListFragment with RecyclerView)
        replaceFragment(TaskListFragment())

        // Set up bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            resetIcons() // Reset to default icons first

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

    // 🔁 Theme handling
    private fun applySavedTheme() {
        val sharedPrefs = getSharedPreferences("ThemePreferences", MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("DarkModeEnabled", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // 🔄 Switch to the selected fragment
    /*private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }*/

    // 🎨 Reset all icons to default state
    private fun resetIcons() {
        binding.bottomNavigation.menu.findItem(R.id.nav_tasks).setIcon(R.drawable.ic_tasks)
        binding.bottomNavigation.menu.findItem(R.id.nav_calendar).setIcon(R.drawable.ic_calendar)
        binding.bottomNavigation.menu.findItem(R.id.nav_blank).setIcon(R.drawable.ic_blank)
    }
}
