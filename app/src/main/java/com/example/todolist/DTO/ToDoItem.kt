package com.example.todolist.DTO

class ToDoItem() {

    var id: Long = -1
    var toDoId: Long = -1
    var itemName = ""
    var toDoItemCalendarDay = ""
    var toDoItemCalendarMonth = ""
    var toDoItemCalendarYear = ""
    var toDoItemAlarmHour = ""
    var toDoItemAlarmMinutes = ""
    var toDoItemAlarmTimeInterval = ""
    var isCompleted = false
    var deletedFlag = false

}