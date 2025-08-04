package com.example.model.db.dao

import com.example.model.db.tables.TaskTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TaskTable)
    var name by TaskTable.name
    var description by TaskTable.description
    var priority by TaskTable.priority
}