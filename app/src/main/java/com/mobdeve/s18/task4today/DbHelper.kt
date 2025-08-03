package com.mobdeve.s18.task4today
/*
    MOBICOM S18 Group 6
    Jeremiah Ang
    Charles Duelas
    Justin Lee
 */

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// DbHelper - Handles Database Queries and Operations
class DbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DbReferences.DATABASE_NAME,
    null,
    DbReferences.DATABASE_VERSION
){

    private object DbReferences{
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "task4today_db"

        // Task Header Table (example: ITSECWB, ITDBADM)
        const val HEADERS_TABLE = "headers"
        const val HEADER_ID = "id"
        const val HEADER_TITLE = "title"
        const val HEADER_COLOR = "color"

        // Tasks Table and Column Names
        const val TASKS_TABLE = "tasks"
        const val TASK_ID = "id"
        const val TASK_HEADER_ID = "header_id"
        const val TASK = "task"
        const val STATUS = "status"
        const val DATE = "date"
        const val TIME = "time"

        // Task Headers Table SQL Statements
        val CREATE_HEADERS_TABLE = """
            CREATE TABLE $HEADERS_TABLE(
            $HEADER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $HEADER_TITLE TEXT,
            $HEADER_COLOR TEXT
            );
        """.trimIndent()

        const val DROP_HEADERS_TABLE = "DROP TABLE $HEADERS_TABLE"

        // Tasks Table SQL Statements
        val CREATE_TASKS_TABLE = """
            CREATE TABLE $TASKS_TABLE(
                $TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TASK_HEADER_ID INTEGER,
                $TASK TEXT,
                $STATUS INTEGER,
                $DATE TEXT,
                $TIME TEXT,
                FOREIGN KEY($TASK_HEADER_ID) REFERENCES $HEADERS_TABLE($HEADER_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        const val DROP_TASKS_TABLE = "DROP TABLE $TASKS_TABLE"
    }

    private lateinit var sqliteDatabase : SQLiteDatabase

    // Run SQL stmt to CREATE TABLES
    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(DbReferences.CREATE_HEADERS_TABLE)
        db.execSQL(DbReferences.CREATE_TASKS_TABLE)
        //db.execSQL(DbReferences.INSERT_MY_HEADER) // insert sample header on first run of app
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        // Drop table if new version of DB exists
        db.execSQL(DbReferences.DROP_HEADERS_TABLE)
        db.execSQL(DbReferences.DROP_TASKS_TABLE)

        // Create tables again
        onCreate(db)
    }

    // Inserts headers into the db
    fun insertHeaders(header: HeaderModel) {
        sqliteDatabase = writableDatabase
        val cv = ContentValues().apply { // SQLite auto-generates the ID
            put(DbReferences.HEADER_TITLE, header.title) //Set header title
            put(DbReferences.HEADER_COLOR, header.color)
        }
        sqliteDatabase.insert(DbReferences.HEADERS_TABLE, null, cv)
    }

    // Inserts tasks into the db
    fun insertTasks(task : TaskModel){
        sqliteDatabase = writableDatabase
        val cv = ContentValues().apply {
            put(DbReferences.TASK_HEADER_ID, task.header_id)
            put(DbReferences.TASK, task.task) //in Java: task.getTask()
            put(DbReferences.STATUS, task.status)
            put(DbReferences.DATE, task.date)
            put(DbReferences.TIME, task.time)
        }

        sqliteDatabase.insert(DbReferences.TASKS_TABLE, null, cv)
    }

    // Fetch all tasks from the db
    // CHANGE: Called in getAllHeaders() to sort tasks by category (2025/7/27, 12:04AM)
    // Fetch all tasks from the db for a specific date
    fun getAllTasks(date: String) : ArrayList<TaskModel> {
        val taskList = ArrayList<TaskModel>()

        readableDatabase.use { db ->
            db.beginTransaction()
            try {
                db.query(
                    DbReferences.TASKS_TABLE,
                    null, // All columns
                    "${DbReferences.DATE} = ?", // WHERE clause
                    arrayOf(date), // Date parameter
                    null, null, null
                ).use { cursor ->
                    // While cursor is not null
                    if (cursor.moveToFirst()) {
                        do {
                            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.TASK_ID))
                            val header_id = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.TASK_HEADER_ID))
                            val taskItem = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.TASK))
                            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.STATUS))
                            val time = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.TIME))

                            val task = TaskModel(id, header_id, status, taskItem, date, time)
                            taskList.add(task)

                        } while (cursor.moveToNext())
                    } // end of if statement
                } // end of cursor use

                db.setTransactionSuccessful()
            } // end of try block
            finally {
                db.endTransaction()
            }
        }

        return taskList
    }


    // Fetch all Task Headers (Categories) from the db
    fun getAllHeaders(currentDate: String) : ArrayList<HeaderModel> {
        val headerList = ArrayList<HeaderModel>()
        val allTasks = getAllTasks(currentDate) // Get tasks for the specific date

        readableDatabase.use { db ->
            db.beginTransaction()
            try {
                db.query(
                    DbReferences.HEADERS_TABLE,
                    null, null, null, null, null, null
                ).use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.HEADER_ID))
                            val title = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.HEADER_TITLE))
                            val color = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.HEADER_COLOR))

                            // Get only tasks with the correct header ID and current date
                            val taskList = ArrayList(allTasks.filter { it.header_id == id })

                            val header = HeaderModel(id, title, color, taskList)
                            headerList.add(header)

                        } while (cursor.moveToNext())
                    }
                }

                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        return headerList
    }

    fun updateStatus(id : Int, status : Int){
        sqliteDatabase = writableDatabase

        val cv = ContentValues()
        cv.put(DbReferences.STATUS, status)
        sqliteDatabase.update(
            DbReferences.TASKS_TABLE,
            cv,
            DbReferences.TASK_ID + "=?",
            arrayOf(id.toString())
        )
    }

    // Update task text
    fun updateTask(id : Int, task : String){
        sqliteDatabase = writableDatabase

        val cv = ContentValues()
        cv.put(DbReferences.TASK, task)
        sqliteDatabase.update(
            DbReferences.TASKS_TABLE,
            cv,
            DbReferences.TASK_ID + "=?",
            arrayOf(id.toString())
        )
    }

    fun deleteTask(id : Int){
        sqliteDatabase = writableDatabase

        sqliteDatabase.delete(
            DbReferences.TASKS_TABLE,
            DbReferences.TASK_ID + "=?",
            arrayOf(id.toString())
        )
    }

    // Delete Task Header
    fun deleteHeader(id : Int){
        sqliteDatabase = writableDatabase

        sqliteDatabase.delete(
            DbReferences.HEADERS_TABLE,
            DbReferences.HEADER_ID + "=?",
            arrayOf(id.toString())
        )
    }


}