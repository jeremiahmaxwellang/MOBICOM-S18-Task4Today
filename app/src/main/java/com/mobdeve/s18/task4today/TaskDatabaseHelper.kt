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
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createHeaderTable = """
            CREATE TABLE $TABLE_HEADER (
                $COLUMN_HEADER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HEADER_TITLE TEXT,
                $COLUMN_HEADER_COLOR TEXT
            );
        """.trimIndent()

        val createTaskTable = """
            CREATE TABLE $TABLE_TASK (
                $COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK_HEADER_ID INTEGER,
                $COLUMN_TASK_TITLE TEXT,
                $COLUMN_TASK_TIME TEXT,
                $COLUMN_TASK_IS_CHECKED INTEGER,
                FOREIGN KEY($COLUMN_TASK_HEADER_ID) REFERENCES $TABLE_HEADER($COLUMN_HEADER_ID)
            );
        """.trimIndent()

        db.execSQL(createHeaderTable)
        db.execSQL(createTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HEADER")
        onCreate(db)
    }

    fun insertHeader(title: String, color: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_HEADER_TITLE, title)
            put(COLUMN_HEADER_COLOR, color)
        }
        return writableDatabase.insert(TABLE_HEADER, null, values)
    }

    fun insertTask(headerId: Long, title: String, time: String, isChecked: Boolean): Long {
        val values = ContentValues().apply {
            put(COLUMN_TASK_HEADER_ID, headerId)
            put(COLUMN_TASK_TITLE, title)
            put(COLUMN_TASK_TIME, time)
            put(COLUMN_TASK_IS_CHECKED, if (isChecked) 1 else 0)
        }
        return writableDatabase.insert(TABLE_TASK, null, values)
    }

    fun isEmpty(): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_HEADER", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    fun getAllItems(): List<ListItem> {
        val db = readableDatabase
        val result = mutableListOf<ListItem>()

        val headerCursor = db.rawQuery("SELECT * FROM $TABLE_HEADER", null)
        while (headerCursor.moveToNext()) {
            val headerId = headerCursor.getLong(headerCursor.getColumnIndexOrThrow(COLUMN_HEADER_ID))
            val headerTitle = headerCursor.getString(headerCursor.getColumnIndexOrThrow(COLUMN_HEADER_TITLE))
            result.add(ListItem.Header(headerTitle))

            val taskCursor = db.rawQuery(
                "SELECT * FROM $TABLE_TASK WHERE $COLUMN_TASK_HEADER_ID = ?",
                arrayOf(headerId.toString())
            )
            while (taskCursor.moveToNext()) {
                val title = taskCursor.getString(taskCursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE))
                val time = taskCursor.getString(taskCursor.getColumnIndexOrThrow(COLUMN_TASK_TIME))
                val isChecked = taskCursor.getInt(taskCursor.getColumnIndexOrThrow(COLUMN_TASK_IS_CHECKED)) == 1
                result.add(ListItem.Task(title, time, isChecked))
            }
            taskCursor.close()
        }
        headerCursor.close()

        return result
    }
}
