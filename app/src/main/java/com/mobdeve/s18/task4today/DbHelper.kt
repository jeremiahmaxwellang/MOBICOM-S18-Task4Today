package com.mobdeve.s18.task4today

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DbReferences.DATABASE_NAME,
    null,
    DbReferences.DATABASE_VERSION
){

    private object DbReferences{
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "task4today_db"

//        TODO: DB Design should have Task Lists (example: ITSECWB, ITDBADM)
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

        val DROP_HEADERS_TABLE = "DROP TABLE " + HEADERS_TABLE

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

        val DROP_TASKS_TABLE = "DROP TABLE " + TASKS_TABLE

    }

    private lateinit var sqliteDatabase : SQLiteDatabase

    // Run SQL stmt to CREATE TABLES
    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(DbReferences.CREATE_HEADERS_TABLE)
        db.execSQL(DbReferences.CREATE_TASKS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        // Drop table if new version of DB exists
        db.execSQL(DbReferences.DROP_HEADERS_TABLE)
        db.execSQL(DbReferences.DROP_TASKS_TABLE)

        // Create tables again
        onCreate(db)
    }

    // Inserts headers into the db
    // TODO: Call insertHeaders where needed
    // TODO: Make update & delete functions for headers
    fun insertHeaders(header: HeaderModel) {
        sqliteDatabase = writableDatabase
        val cv = ContentValues().apply { // SQLite auto-generates the ID
            put(DbReferences.HEADER_TITLE, header.title)
            put(DbReferences.HEADER_COLOR, header.color)
        }
        sqliteDatabase.insert(DbReferences.HEADERS_TABLE, null, cv)
    }

    // Inserts tasks into the db
    fun insertTasks(task : TaskModel){
        sqliteDatabase = writableDatabase
        var cv = ContentValues().apply {
            put(DbReferences.TASK_HEADER_ID, task.header_id)
            put(DbReferences.TASK, task.task) //in Java: task.getTask()
            put(DbReferences.STATUS, task.status)
        }

        sqliteDatabase.insert(DbReferences.TASKS_TABLE, null, cv)
    }

    // Fetch all tasks from the db
    // CHANGE: Called in getAllHeaders() to sort tasks by category (2025/7/27, 12:04AM)
    fun getAllTasks() : ArrayList<TaskModel>{
        val taskList = ArrayList<TaskModel>()

        readableDatabase.use { db ->
            db.beginTransaction()
            try{
                db.query(
                    DbReferences.TASKS_TABLE,
                    null, null, null, null, null, null
                ).use { cursor ->
                        // While cursor is not null
                        if (cursor.moveToFirst()) {
                            do {
                                var id = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.TASK_ID))
                                var header_id = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.TASK_HEADER_ID))
                                var taskItem = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.TASK))
                                var status = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.STATUS))
                                var date = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.DATE))
                                var time = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.TIME))

                                val task = TaskModel(id, header_id, status, taskItem, date, time)
                                taskList.add(task)

                            } while (cursor.moveToNext())
                        } // end of if statement
                    } //end of .use { cursor ->

                db.setTransactionSuccessful()
            } // end of try statement
            finally{
                db.endTransaction()
            }
        } //end of readableDatabase.use

        return taskList

    }

    // Fetch all Task Headers (Categories) from the db
    // TODO: call this function in MainActivity or the Tasks Activity
    fun getAllHeaders() : ArrayList<HeaderModel>{
        val headerList = ArrayList<HeaderModel>()
        val allTasks = getAllTasks() // list of ALL tasks

        readableDatabase.use { db ->
            db.beginTransaction()
            try{
                db.query(
                    DbReferences.HEADERS_TABLE,
                    null, null, null, null, null, null
                ).use { cursor ->
                    if(cursor.moveToFirst()){
                        do {
                            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.HEADER_ID))
                            val title = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.HEADER_TITLE))
                            val color = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.HEADER_COLOR))

                            // list of tasks under this header (category)
                            val taskList = ArrayList(allTasks.filter{ it.header_id == id })

                            val header = HeaderModel(id, title, color, taskList)

                            headerList.add(header)

                        } while (cursor.moveToNext())
                    } // end of if statement
                } //end of .use { cursor ->

                db.setTransactionSuccessful()
            } // end of try statement
            finally{
                db.endTransaction()
            }
        } //end of readableDatabase.use

        return headerList
    }

    fun updateStatus(id : Int, status : Int){
        sqliteDatabase = writableDatabase

        var cv = ContentValues()
        cv.put(DbReferences.STATUS, status)
        sqliteDatabase.update(
            DbReferences.TASKS_TABLE,
            cv,
            DbReferences.TASK_ID + "=?",
            arrayOf(id.toString())
        )
    }

    fun updateTask(id : Int, task : String){
        sqliteDatabase = writableDatabase

        var cv = ContentValues()
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

    // Update Header TITLE
    fun updateHeaderTitle(id : Int, title : String){
        sqliteDatabase = writableDatabase

        var cv = ContentValues()
        cv.put(DbReferences.HEADER_TITLE, title)
        sqliteDatabase.update(
            DbReferences.HEADERS_TABLE,
            cv,
            DbReferences.HEADER_ID + "=?",
            arrayOf(id.toString())
        )
    }

    // Update Header COLOR
    fun updateHeaderColor(id : Int, color : String){
        sqliteDatabase = writableDatabase

        var cv = ContentValues()
        cv.put(DbReferences.HEADER_COLOR, color)
        sqliteDatabase.update(
            DbReferences.HEADERS_TABLE,
            cv,
            DbReferences.HEADER_ID + "=?",
            arrayOf(id.toString())
        )
    }

    fun deleteHeader(id : Int){
        sqliteDatabase = writableDatabase

        sqliteDatabase.delete(
            DbReferences.TASKS_TABLE,
            DbReferences.TASK_ID + "=?",
            arrayOf(id.toString())
        )
    }


}