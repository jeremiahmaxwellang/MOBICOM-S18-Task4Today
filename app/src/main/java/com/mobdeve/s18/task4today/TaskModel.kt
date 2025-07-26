package com.mobdeve.s18.task4today

class TaskModel(
    id: Int,
    header_id: Int,
    status: Int,
    task: String,
    date: String,
    time: String
) {

// Getters and Setters

    var id: Int = id
        private set

    var header_id: Int = header_id
        private set

    var status: Int = status
        private set

    var task : String = task
        private set

    var date : String = date
        private set

    var time : String = time
        private set

    // Constructor without ID
    constructor(status: Int, task: String, date: String, time: String) : this(0, 0, status, task, date, time)

}