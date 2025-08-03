package com.mobdeve.s18.task4today

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