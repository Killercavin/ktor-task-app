package com.example.plugins

import com.example.model.FakeTaskRepository
import com.example.model.Priority
import com.example.model.Task
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val repository = FakeTaskRepository()

    routing {
        get("/") {
            call.respondText("Hello World!", ContentType.Text.Plain)
        }

        // GET all tasks
        get("/tasks") {
            val allTasks = repository.getAllTasks()
            call.respond(HttpStatusCode.OK, allTasks)
        }

        // GET a task by name
        get("/tasks/name/{name?}") {
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
        get("/tasks/priority/{priority?}") {
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

        // POST a new task
        post("/tasks") {
            val task = try {
                call.receive<Task>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            if (task.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Name is required")
                return@post
            }
            if (task.description.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Description is required")
                return@post
            }
            // Redundant, but good for extra safety or better error handling
            if (!Priority.entries.contains(task.priority)) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            repository.addTask(task)
            call.respond(HttpStatusCode.Created, task)
        }

        // DELETE a task by name
        delete("/tasks/delete/{name?}") {
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
