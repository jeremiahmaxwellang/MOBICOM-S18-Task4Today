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
