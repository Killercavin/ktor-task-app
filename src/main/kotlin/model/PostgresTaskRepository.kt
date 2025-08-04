package com.example.model

import com.example.model.db.dao.TaskDAO
import com.example.model.db.tables.TaskTable
import com.example.model.mapper.suspendTransaction
import com.example.model.mapper.taskDaoToModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class PostgresTaskRepository: TaskRepository {
    override suspend fun getAllTasks(): List<Task> {
        return suspendTransaction {
            TaskDAO.all().map { taskDaoToModel(it) }
        }
    }

    override suspend fun addTask(task: Task) {
        suspendTransaction {
            TaskDAO.new {
                name = task.name
                description = task.description
                priority = task.priority.toString()
            }
        }
    }

    override suspend fun byName(name: String): Task? {
        return suspendTransaction {
            TaskDAO.find {
                (TaskTable.name eq name)
            }.map { taskDaoToModel(it) }.firstOrNull()
        }
    }

    override suspend fun byPriority(priority: Priority): List<Task> {
        return suspendTransaction {
            TaskDAO.find { TaskTable.priority eq priority.toString() }.map { taskDaoToModel(it) }
        }
    }

    override suspend fun removeTask(name: String): Boolean {
        return suspendTransaction {
            val rowDeleted = TaskTable.deleteWhere { TaskTable.name eq name }
            rowDeleted == 1
        }
    }
}