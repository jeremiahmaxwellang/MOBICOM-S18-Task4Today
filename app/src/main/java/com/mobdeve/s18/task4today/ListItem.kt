package com.mobdeve.s18.task4today

sealed class ListItem {
    // Modified Header to include a unique headerId and color
    data class Header(val headerId: Long, val title: String, val color: String) : ListItem()

    // Task remains the same but each task is associated with a headerId
    data class Task(val title: String, val time: String, val isChecked: Boolean) : ListItem()
}