package com.example.todolist.DTO

class ToDo {

    var id: Long = -1
    var toDoAlarm = ""
    var name = ""
    var createdAt = ""
    var items: MutableList<ToDoItem> = ArrayList()
    var deletedFlag = false

}