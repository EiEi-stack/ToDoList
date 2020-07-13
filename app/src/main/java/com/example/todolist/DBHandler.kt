package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.content.LocusId
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItem

class DBHandler(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createToDoTable = "CREATE TABLE $TABLE_TODO (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar," +
                "$COL_TODO_CALENDAR_DAY varchar," +
                "$COL_TODO_CALENDAR_MONTH varchar," +
                "$COL_TODO_CALENDAR_YEAR varchar," +
                "$COL_TODO_ALARM_HOUR," +
                "$COL_TODO_ALARM_MINUTE," +
                "$COL_TODO_ALARM_TIME_INTERVAL," +
                "$COL_IS_DELETED integer);"
        val createToDoItemTable =
            "CREATE TABLE $TABLE_TODO_ITEM (" +
                    "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                    "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                    "$COL_TODO_ID integer," +
                    "$COL_ITEM_NAME varchar," +
                    "$COL_IS_COMPLETED integer," +
                    "$COL_TODO_ITEM_ALARM varchar," +
                    "$COL_TODO_ITEM_CALENDAR_DAY varchar," +
                    "$COL_TODO_ITEM_CALENDAR_MONTH varchar," +
                    "$COL_TODO_ITEM_CALENDAR_YEAR varchar," +
                    "$COL_TODO_ITEM_ALARM_HOUR varchar," +
                    "$COL_TODO_ITEM_ALARM_MINUTE varchar" +
                    "$COL_TODO_ITEM_ALARM_TIME_INTERVAL varchar," +
                    "$COL_IS_DELETED integer);"

        db.execSQL(createToDoTable)
        db.execSQL(createToDoItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun addToDo(toDo: ToDo): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        cv.put(COL_TODO_ALARM_HOUR, toDo.toDoAlarmHour)
        cv.put(COL_TODO_ALARM_MINUTE, toDo.toDoAlarmMinutes)
        cv.put(COL_TODO_CALENDAR_YEAR, toDo.toDoCalendarYear)
        cv.put(COL_TODO_CALENDAR_MONTH, toDo.toDoCalendarMonth)
        cv.put(COL_TODO_CALENDAR_DAY, toDo.toDoCalendarDay)
        val result: Long = db.insert(TABLE_TODO, null, cv)
        return result != (-1).toLong()

    }

    fun updateToDo(toDo: ToDo) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        cv.put(COL_TODO_ALARM_HOUR, toDo.toDoAlarmHour)
        cv.put(COL_TODO_ALARM_MINUTE, toDo.toDoAlarmMinutes)
        cv.put(COL_TODO_CALENDAR_YEAR, toDo.toDoCalendarYear)
        cv.put(COL_TODO_CALENDAR_MONTH, toDo.toDoCalendarMonth)
        cv.put(COL_TODO_CALENDAR_DAY, toDo.toDoCalendarDay)
        db.update(TABLE_TODO, cv, "$COL_ID=?", arrayOf(toDo.id.toString()))

    }

    fun deleteToDo(todoId: Long) {
        val db = writableDatabase
        db.delete(TABLE_TODO_ITEM, "$COL_TODO_ID=?", arrayOf(todoId.toString()))
        db.delete(TABLE_TODO, "$COL_ID=?", arrayOf(todoId.toString()))
//        deleteToDoItem(todoId)
//        val queryResult =
//            db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$todoId", null)
//
//        if (queryResult.moveToFirst()) {
//            do {
//                val todo = ToDo()
//                todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
//                todo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
//                todo.toDoAlarmHour = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ALARM_HOUR
//                    )
//                )
//                todo.toDoAlarmMinutes = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ALARM_MINUTE
//                    )
//                )
//                todo.toDoCalendarYear = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_CALENDAR_YEAR
//                    )
//                )
//                todo.toDoCalendarMonth = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_CALENDAR_MONTH
//                    )
//                )
//                todo.toDoCalendarDay = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_CALENDAR_DAY
//                    )
//                )
//                todo.isDeleted = true
//                updateToDo(todo)
//            } while (queryResult.moveToNext())
//        }
//        queryResult.close()
    }

    fun updateToDOItemCompletedStatus(todoId: Long, isCompleted: Boolean) {
        val db = writableDatabase
        val queryResult =
            db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$todoId", null)

        if (queryResult.moveToFirst()) {
            do {
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = isCompleted
                updateToDoItem(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
    }

    fun getToDo(): MutableList<ToDo> {
        val result: MutableList<ToDo> = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * from $TABLE_TODO", null)
        if (queryResult.moveToFirst()) {
            do {
                val todo = ToDo()
                todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                todo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                todo.toDoAlarmHour = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ALARM_HOUR
                    )
                )
                todo.toDoAlarmMinutes = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ALARM_MINUTE
                    )
                )
                todo.toDoCalendarYear = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_CALENDAR_YEAR
                    )
                )
                todo.toDoCalendarMonth = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_CALENDAR_MONTH
                    )
                )
                todo.toDoCalendarDay = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_CALENDAR_DAY
                    )
                )
                result.add(todo)
            } while (queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

    fun addToDoItem(item: ToDoItem): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)
        cv.put(COL_TODO_ITEM_ALARM_HOUR, item.toDoItemAlarmHour)
        cv.put(COL_TODO_ITEM_ALARM_MINUTE, item.toDoItemAlarmMinutes)
        cv.put(COL_TODO_ITEM_CALENDAR_YEAR, item.toDoItemCalendarYear)
        cv.put(COL_TODO_ITEM_CALENDAR_MONTH, item.toDoItemCalendarMonth)
        cv.put(COL_TODO_ITEM_CALENDAR_DAY, item.toDoItemCalendarDay)
        if (item.isCompleted)
            cv.put(COL_IS_COMPLETED, true)
        else
            cv.put(COL_IS_COMPLETED, false)
        val result = db.insert(TABLE_TODO_ITEM, null, cv)
        return result != (-1).toLong()

    }


    fun getToDoItems(todoId: Long): MutableList<ToDoItem> {
        val result: MutableList<ToDoItem> = ArrayList()

        val db = readableDatabase
        val queryResult =
            db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$todoId", null)

        if (queryResult.moveToFirst()) {
            do {
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.toDoItemAlarmHour = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_ALARM_HOUR
                    )
                )
                item.toDoItemAlarmMinutes = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_ALARM_MINUTE
                    )
                )
                item.toDoItemCalendarYear = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_CALENDAR_YEAR
                    )
                )
                item.toDoItemCalendarMonth = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_CALENDAR_MONTH
                    )
                )
                item.toDoItemCalendarDay = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_CALENDAR_DAY
                    )
                )
                item.isCompleted =
                    queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETED)) == 1
                result.add(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }


    fun getToDoItemsCompleted(): MutableList<ToDoItem> {
        val result: MutableList<ToDoItem> = ArrayList()

        val db = readableDatabase
        val queryResult =
            db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_IS_COMPLETED=1", null)

        if (queryResult.moveToFirst()) {
            do {
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.toDoItemAlarmHour = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_ALARM_HOUR
                    )
                )
                item.toDoItemAlarmMinutes = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_ALARM_MINUTE
                    )
                )
                item.toDoItemCalendarYear = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_CALENDAR_YEAR
                    )
                )
                item.toDoItemCalendarMonth = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_CALENDAR_MONTH
                    )
                )
                item.toDoItemCalendarDay = queryResult.getString(
                    queryResult.getColumnIndex(
                        COL_TODO_ITEM_CALENDAR_DAY
                    )
                )
                item.isCompleted =
                    queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETED)) == 1
                result.add(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

    fun updateToDoItem(item: ToDoItem) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)
        cv.put(COL_IS_COMPLETED, item.isCompleted)
        cv.put(COL_TODO_ITEM_ALARM_HOUR, item.toDoItemAlarmHour)
        cv.put(COL_TODO_ITEM_ALARM_MINUTE, item.toDoItemAlarmMinutes)
        cv.put(COL_TODO_ITEM_CALENDAR_YEAR, item.toDoItemCalendarYear)
        cv.put(COL_TODO_ITEM_CALENDAR_MONTH, item.toDoItemCalendarMonth)
        cv.put(COL_TODO_ITEM_CALENDAR_DAY, item.toDoItemCalendarDay)
        db.update(TABLE_TODO_ITEM, cv, "$COL_ID=?", arrayOf(item.id.toString()))
    }

    fun deleteToDoItem(itemId: Long) {
        val db = writableDatabase
        db.delete(TABLE_TODO_ITEM, "$COL_ID=?", arrayOf(itemId.toString()))
//        val queryResult =
//            db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$itemId", null)
//
//        if (queryResult.moveToFirst()) {
//            do {
//                val item = ToDoItem()
//                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
//                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
//                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
//                item.toDoItemAlarmHour = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ITEM_ALARM_HOUR
//                    )
//                )
//                item.toDoItemAlarmMinutes = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ITEM_ALARM_MINUTE
//                    )
//                )
//                item.toDoItemCalendarYear = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ITEM_CALENDAR_YEAR
//                    )
//                )
//                item.toDoItemCalendarMonth = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ITEM_CALENDAR_MONTH
//                    )
//                )
//                item.toDoItemCalendarDay = queryResult.getString(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ITEM_CALENDAR_DAY
//                    )
//                )
//                item.isCompleted = queryResult.getInt(
//                    queryResult.getColumnIndex(
//                        COL_TODO_ITEM_CALENDAR_DAY
//                    )
//                ) == 1
//                item.isDeleted = true
//                updateToDoItem(item)
//            } while (queryResult.moveToNext())
//        }
//
//        queryResult.close()
    }
}