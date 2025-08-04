package com.example.routes

import com.example.model.PostgresTaskRepository
import com.example.model.Priority
import com.example.model.Task
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoute() {
    val repository = PostgresTaskRepository()

    route("/tasks") {

        // GET all tasks
        get {
            val allTasks = repository.getAllTasks()
            call.respond(HttpStatusCode.OK, allTasks)
        }

        // POST a new task
        post {
            val task = call.receive<Task>()

            if (task.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Name is required")
                return@post
            }
            if (task.description.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Description is required")
                return@post
            }
            // Redundant, but good for extra safety or better error handling
            // ISSUE
            if (task.priority.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Priority is required")
                return@post
            }

            if (repository.byName(task.name) != null) {
                call.respond(HttpStatusCode.Conflict, "Task with that name already exists")
                return@post
            }

            try {
                repository.addTask(task)
                call.respond(HttpStatusCode.Created, task)
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }
        }

        // GET a task by name
        get("/name/{name?}") {
            val parameterName = call.parameters["name"]
            if (parameterName == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing task name parameter")
                return@get
            }

            val task = repository.byName(parameterName)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound, "Task with name '$parameterName' not found")
                return@get
            }

            call.respond(HttpStatusCode.OK, task)
        }

        // GET task(s) by priority
        get("/priority/{priority?}") {
            val parameterPriority = call.parameters["priority"]
            if (parameterPriority == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing priority parameter")
                return@get
            }

            val priority = try {
                Priority.valueOf(parameterPriority)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid priority value, $parameterPriority")
                return@get
            }

            val tasks = repository.byPriority(priority)
            if (tasks.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "Task with priority '$priority' not found")
                return@get
            }

            call.respond(HttpStatusCode.OK, tasks)
        }

        // DELETE a task by name
        delete("/delete/{name?}") {
            val name = call.parameters["name"]
            if (name == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing task name parameter")
                return@delete
            }

            if (repository.removeTask(name)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Task with name '$name' not found")
            }
        }
    }
}