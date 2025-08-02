package com.example.model

interface TaskRepository {
    // fetch all tasks
    fun getAllTasks(): List<Task>

    // POST a task
    fun addTask(task: Task)

    // GET task by name
    fun byName(name: String): Task?

    // GET task by priority
    fun byPriority(priority: Priority): List<Task>

    // DELETE a task
    fun removeTask(name: String): Boolean
}