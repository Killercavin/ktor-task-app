package com.example.model

interface TaskRepository {
    // fetch all tasks
    suspend fun getAllTasks(): List<Task>

    // POST a task
    suspend fun addTask(task: Task)

    // GET task by name
    suspend fun byName(name: String): Task?

    // GET task by priority
    suspend fun byPriority(priority: Priority): List<Task>

    // DELETE a task
    suspend fun removeTask(name: String): Boolean
}