package com.example.model

object TaskRepository {
    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium),
    )

    // fetch all tasks
    fun allTasks(): List<Task> = tasks

    // fetch a task(s) by priority
    fun tasksByPriority(priority: Priority) = tasks.filter { it.priority == priority }

    // fetch a task by its name
    fun taskByName(name: String) {
        tasks.find { it.name.equals(name, ignoreCase = true) }
    }

    // add a new task
    fun addTask(task: Task) {
        if (task == taskByName(task.name)) {
            throw IllegalArgumentException("Task with name ${task.name} already exists") // tasks are unique so no duplicates
        }
        tasks.add(task)
    }
}