package com.mobdeve.s18.task4today

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "task_db"
        const val DATABASE_VERSION = 1

        const val TABLE_HEADER = "task_header"
        const val COLUMN_HEADER_ID = "id"
        const val COLUMN_HEADER_TITLE = "title"
        const val COLUMN_HEADER_COLOR = "color"

        const val TABLE_TASK = "task"
        const val COLUMN_TASK_ID = "id"
        const val COLUMN_TASK_HEADER_ID = "header_id"
        const val COLUMN_TASK_TITLE = "title"
        const val COLUMN_TASK_TIME = "time"
        const val COLUMN_TASK_IS_CHECKED = "is_checked"
        const val COLUMN_TASK_DATE = "date"  // New column for the task date
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createHeaderTable = """
            CREATE TABLE $TABLE_HEADER (
                $COLUMN_HEADER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HEADER_TITLE TEXT,
                $COLUMN_HEADER_COLOR TEXT
            );
        """.trimIndent()

        // Modify task table creation to include the date
        val createTaskTable = """
            CREATE TABLE $TABLE_TASK (
                $COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK_HEADER_ID INTEGER,
                $COLUMN_TASK_TITLE TEXT,
                $COLUMN_TASK_TIME TEXT,
                $COLUMN_TASK_IS_CHECKED INTEGER,
                $COLUMN_TASK_DATE TEXT,
                FOREIGN KEY($COLUMN_TASK_HEADER_ID) REFERENCES $TABLE_HEADER($COLUMN_HEADER_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        db.execSQL(createHeaderTable)
        db.execSQL(createTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle schema upgrades here without dropping the tables.
        // For example, you can use ALTER TABLE to modify the existing structure.
        if (oldVersion < 2) {
            // Add new column to the task table if needed (example for schema change)
            db.execSQL("ALTER TABLE $TABLE_TASK ADD COLUMN $COLUMN_TASK_DATE TEXT")
        }
        // You can handle other version-specific schema changes here
    }

    // Insert header and return the unique headerId
    fun insertHeader(title: String, color: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_HEADER_TITLE, title)
            put(COLUMN_HEADER_COLOR, color)
        }
        return writableDatabase.insert(TABLE_HEADER, null, values)  // Auto-generated unique headerId
    }

    // Insert task under the specific header with date
    fun insertTask(headerId: Long, title: String, time: String, isChecked: Boolean, date: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_TASK_HEADER_ID, headerId)
            put(COLUMN_TASK_TITLE, title)
            put(COLUMN_TASK_TIME, time)
            put(COLUMN_TASK_IS_CHECKED, if (isChecked) 1 else 0)
            put(COLUMN_TASK_DATE, date)  // Store the date
        }
        return writableDatabase.insert(TABLE_TASK, null, values)
    }

    // Check if the database is empty
    fun isEmpty(): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_HEADER", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    // Get all headers and their tasks
    fun getAllItems(): List<ListItem> {
        val db = readableDatabase
        val result = mutableListOf<ListItem>()

        // Query all headers
        val headerCursor = db.rawQuery("SELECT * FROM $TABLE_HEADER", null)
        while (headerCursor.moveToNext()) {
            val headerId = headerCursor.getLong(headerCursor.getColumnIndexOrThrow(COLUMN_HEADER_ID))
            val headerTitle = headerCursor.getString(headerCursor.getColumnIndexOrThrow(COLUMN_HEADER_TITLE))
            val headerColor = headerCursor.getString(headerCursor.getColumnIndexOrThrow(COLUMN_HEADER_COLOR)) // Get color
            result.add(ListItem.Header(headerId, headerTitle, headerColor))  // Add header with unique headerId and color

            // Query tasks for the current header
            val taskCursor = db.rawQuery(
                "SELECT * FROM $TABLE_TASK WHERE $COLUMN_TASK_HEADER_ID = ?",
                arrayOf(headerId.toString())
            )
            while (taskCursor.moveToNext()) {
                val title = taskCursor.getString(taskCursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE))
                val time = taskCursor.getString(taskCursor.getColumnIndexOrThrow(COLUMN_TASK_TIME))
                val isChecked = taskCursor.getInt(taskCursor.getColumnIndexOrThrow(COLUMN_TASK_IS_CHECKED)) == 1
                val date = taskCursor.getString(taskCursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)) // Get the date
                result.add(ListItem.Task(title, time, isChecked, date))  // Add task with date under the correct header
            }
            taskCursor.close()
        }
        headerCursor.close()

        return result
    }

    // Get tasks for a specific date
    fun getTasksForDate(date: String): List<ListItem> {
        val db = readableDatabase
        val result = mutableListOf<ListItem>()

        // Query to fetch all headers with their tasks for the specified date
        val query = """
    SELECT th.$COLUMN_HEADER_ID, th.$COLUMN_HEADER_TITLE, th.$COLUMN_HEADER_COLOR,
           t.$COLUMN_TASK_TITLE, t.$COLUMN_TASK_TIME, t.$COLUMN_TASK_IS_CHECKED, t.$COLUMN_TASK_DATE
    FROM $TABLE_HEADER th
    JOIN $TABLE_TASK t ON th.$COLUMN_HEADER_ID = t.$COLUMN_TASK_HEADER_ID
    WHERE t.$COLUMN_TASK_DATE = ?
"""

        val cursor = db.rawQuery(query, arrayOf(date))
        var currentHeaderId: Long? = null

        while (cursor.moveToNext()) {
            val headerId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_HEADER_ID))
            val headerTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADER_TITLE))
            val headerColor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADER_COLOR))

            // Only add the header once
            if (headerId != currentHeaderId) {
                result.add(ListItem.Header(headerId, headerTitle, headerColor))  // Add header with unique headerId
                currentHeaderId = headerId  // Update currentHeaderId to the newly processed headerId
            }

            // Add task under the current header
            val taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE))
            val taskTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TIME))
            val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_IS_CHECKED)) == 1
            val taskDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)) // Get the task date

            result.add(ListItem.Task(taskTitle, taskTime, isChecked, taskDate))  // Add task with date under the correct header
        }
        cursor.close()

        return result
    }
}