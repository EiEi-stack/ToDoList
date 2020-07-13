package com.example.todolist

const val DB_NAME = "ToDoList"
const val DB_VERSION = 1
const val TABLE_TODO = "ToDo"
const val COL_ID = "id"
const val COL_CREATED_AT = "createdAt"
const val COL_NAME = "name"
const val COL_TODO_CALENDAR_DAY = "toDoCalendarDay"
const val COL_TODO_CALENDAR_MONTH = "toDoCalendarMonth"
const val COL_TODO_CALENDAR_YEAR = "toDoCalendarYear"
const val COL_TODO_ALARM_HOUR = "toDoAlarmHour"
const val COL_TODO_ALARM_MINUTE = "toDoAlarmMinutes"
const val COL_TODO_ALARM_TIME_INTERVAL = "toDoAlarmTimeInterval"
const val COL_IS_DELETED ="isDeleted"

const val TABLE_TODO_ITEM = "ToDoItem"
const val COL_TODO_ID = "toDoId"
const val COL_TODO_ITEM_ALARM = "toDoItemAlarm"
const val COL_ITEM_NAME = "itemName"
const val COL_IS_COMPLETED = "isCompleted"
const val COL_DELETED_FLG = "deletedFlag"
const val COL_TODO_ITEM_CALENDAR_DAY = "toDoItemCalendarDay"
const val COL_TODO_ITEM_CALENDAR_MONTH = "toDoItemCalendarMonth"
const val COL_TODO_ITEM_CALENDAR_YEAR = "toDoItemCalendarYear"
const val COL_TODO_ITEM_ALARM_HOUR = "toDoItemAlarmHour"
const val COL_TODO_ITEM_ALARM_MINUTE = "toDoItemAlarmMinutes"
const val COL_TODO_ITEM_ALARM_TIME_INTERVAL = "toDoItemAlarmTimeInterval"

const val INTENT_TODO_ID = "TodoId"
const val INTENT_TODO_NAME = "TodoName"

