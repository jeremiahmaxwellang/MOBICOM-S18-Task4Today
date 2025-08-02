package com.mobdeve.s18.task4today

sealed class ListItem {
    data class Header(val headerId: Long, val title: String, val color: String) : ListItem()

    // Modified Task to include date
    data class Task(val title: String, val time: String, val isChecked: Boolean, val date: String) : ListItem()
}