package com.example.model

class FakeTaskRepository : TaskRepository {
    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium),
        Task("deleting", "Delete a house", Priority.Vital)
    )

    override fun getAllTasks(): List<Task> = tasks

    override fun byPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    override fun byName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    override fun addTask(task: Task) {
        if (tasks.contains(byName(task.name))) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }

    override fun removeTask(name: String): Boolean {
        return tasks.removeIf { it.name == name }
    }
}