package com.mobdeve.s18.task4today
/*
    MOBICOM S18 Group 6
    Jeremiah Ang
    Charles Duelas
    Justin Lee
 */

// Model for Task Group HEADERS
class HeaderModel(
    id: Int,
    title: String,
    color: String,
    var taskList: ArrayList<TaskModel>
) {

// Getters and Setters

    var id: Int = id
        private set

    var title: String = title
        private set

    var color : String = color
        private set

}