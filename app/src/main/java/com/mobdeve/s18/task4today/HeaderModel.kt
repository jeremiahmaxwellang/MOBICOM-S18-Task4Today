package com.mobdeve.s18.task4today

class HeaderModel(
    id: Int,
    title: String,
    color: String,
    taskList: ArrayList<TaskModel> // list of tasks under this header
) {

// Getters and Setters

    var id: Int = id
        private set

    var title: String = title
        private set

    var color : String = color
        private set

    var taskList : ArrayList<TaskModel> = taskList

    // Constructor without ID
    constructor(title: String, color: String, taskList : ArrayList<TaskModel>) : this(0, title, color, taskList)

}