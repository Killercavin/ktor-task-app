package com.example.model.mapper

import com.example.model.Priority
import com.example.model.Task
import com.example.model.db.dao.TaskDAO
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T {
    return newSuspendedTransaction(Dispatchers.IO, statement = block)
}

fun taskDaoToModel(taskDao: TaskDAO) = Task(taskDao.name, taskDao.description, Priority.valueOf(taskDao.priority))