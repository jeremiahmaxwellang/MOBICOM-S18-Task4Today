package com.mobdeve.s18.task4today

class ToDoModel(
    id: Int,
    status: Int,
    task: String
) {

// Getters and Setters

    var id: Int = id
        private set

    var status: Int = status
        private set

    var task : String = task
        private set

    // Constructor without ID
    constructor(status: Int, task: String) : this(0, status, task)

}