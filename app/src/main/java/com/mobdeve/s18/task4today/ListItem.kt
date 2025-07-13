package com.mobdeve.s18.task4today

sealed class ListItem {
    data class Header(val title: String) : ListItem()
    data class Task(val title: String, val time: String, val isChecked: Boolean) : ListItem()
}