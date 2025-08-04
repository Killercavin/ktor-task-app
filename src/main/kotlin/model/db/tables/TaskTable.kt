package com.example.model.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object TaskTable: IntIdTable("task") {
    val name = varchar("name", 50)
    val description = varchar("description", 50)
    val priority = varchar("priority", 50)
}