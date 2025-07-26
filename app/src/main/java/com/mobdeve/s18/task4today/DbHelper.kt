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
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "task4today_db"
//        TODO: DB Design should have Task Lists (example: ITSECWB, ITDBADM)

        // Tasks Table and Column Names
        const val TASKS_TABLE = "tasks"
        const val ID = "id"
        const val TASK = "task"
        const val STATUS = "status"

        const val CREATE_TASKS_TABLE = "CREATE TABLE " + TASKS_TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASK + " TEXT, " +
                STATUS + " INTEGER)"

        const val DROP_TASKS_TABLE = "DROP TABLE " + TASKS_TABLE

    }

    private lateinit var sqliteDatabase : SQLiteDatabase

    // Run SQL stmt to CREATE TABLES
    override fun onCreate(db: SQLiteDatabase){

        db.execSQL(DbReferences.CREATE_TASKS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        // Drop table if new version of DB exists
        db.execSQL(DbReferences.DROP_TASKS_TABLE)

        // Create tables again
        onCreate(db)
    }

    // Inserts tasks into the db
    fun insertTasks(task : ToDoModel){
        sqliteDatabase = writableDatabase
        var cv = ContentValues().apply {
            put(DbReferences.TASK, task.task) //in Java: task.getTask()
            put(DbReferences.STATUS, task.status)
        }

        sqliteDatabase.insert(DbReferences.TASKS_TABLE, null, cv)
    }

    // Fetch all tasks from the db
    fun getAllTasks() : ArrayList<ToDoModel>{
        var taskList = ArrayList<ToDoModel>() // return list of tasks

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
                                var id = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.ID))
                                var taskItem = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.TASK))
                                var status = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.STATUS))

                                val task = ToDoModel(id, status, taskItem)
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

    fun updateStatus(id : Int, status : Int){
        sqliteDatabase = writableDatabase

        var cv = ContentValues()
        cv.put(DbReferences.STATUS, status)
        sqliteDatabase.update(
            DbReferences.TASKS_TABLE,
            cv,
            DbReferences.ID + "=?",
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
            DbReferences.ID + "=?",
            arrayOf(id.toString())
        )
    }

    fun deleteTask(id : Int){
        sqliteDatabase = writableDatabase

        sqliteDatabase.delete(
            DbReferences.TASKS_TABLE,
            DbReferences.ID + "=?",
            arrayOf(id.toString())
        )
    }


}