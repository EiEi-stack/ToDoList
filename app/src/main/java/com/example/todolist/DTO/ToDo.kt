package com.example.todolist.DTO

class ToDo {

    var id: Long = -1
    var toDoCalendarDay = ""
    var toDoCalendarMonth = ""
    var toDoCalendarYear = ""
    var toDoAlarmHour = ""
    var toDoAlarmMinutes = ""
    var toDoAlarmTimeInterval = ""
    var name = ""
    var createdAt = ""
    var items: MutableList<ToDoItem> = ArrayList()
    var isDeleted = false

}