package com.mobdeve.s18.task4today

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobdeve.s18.task4today.databinding.ActivityMainBinding
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager

        // Set up the ViewPager2 with a FragmentStateAdapter
        viewPager.adapter = ViewPagerAdapter(this)

        // Set up bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> {
                    viewPager.currentItem = 0
                    updateBottomNavigationIcons(0)
                }
                R.id.nav_calendar -> {
                    viewPager.currentItem = 1
                    updateBottomNavigationIcons(1)
                }
                R.id.nav_blank -> {
                    viewPager.currentItem = 2
                    updateBottomNavigationIcons(2)
                }
            }
            true
        }

        // Sync ViewPager with BottomNavigation on swipe
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateBottomNavigationIcons(position)
            }
        })
    }

    // ViewPager2 Adapter
    inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3 // Number of fragments/pages

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TaskListFragment() // Replace with your fragment
                1 -> CalendarFragment() // Replace with your fragment
                else -> SettingsFragment() // Replace with your fragment
            }
        }
    }

    // Update the bottom navigation icons based on the selected fragment
    private fun updateBottomNavigationIcons(position: Int) {
        when (position) {
            0 -> {
                binding.bottomNavigation.menu.findItem(R.id.nav_tasks)
                    .setIcon(R.drawable.ic_tasks_clicked)
                binding.bottomNavigation.menu.findItem(R.id.nav_calendar)
                    .setIcon(R.drawable.ic_calendar)
                binding.bottomNavigation.menu.findItem(R.id.nav_blank)
                    .setIcon(R.drawable.ic_blank)
            }
            1 -> {
                binding.bottomNavigation.menu.findItem(R.id.nav_tasks)
                    .setIcon(R.drawable.ic_tasks)
                binding.bottomNavigation.menu.findItem(R.id.nav_calendar)
                    .setIcon(R.drawable.ic_calendar_clicked)
                binding.bottomNavigation.menu.findItem(R.id.nav_blank)
                    .setIcon(R.drawable.ic_blank)
            }
            2 -> {
                binding.bottomNavigation.menu.findItem(R.id.nav_tasks)
                    .setIcon(R.drawable.ic_tasks)
                binding.bottomNavigation.menu.findItem(R.id.nav_calendar)
                    .setIcon(R.drawable.ic_calendar)
                binding.bottomNavigation.menu.findItem(R.id.nav_blank)
                    .setIcon(R.drawable.ic_blank_clicked)
            }
        }
    }
}