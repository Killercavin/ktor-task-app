package com.example

import com.example.model.FakeTaskRepository
import com.example.model.Priority
import com.example.model.Task
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val repository = FakeTaskRepository()

    routing {
        staticResources("/", "static")

        // GET tasks
        get("/tasks") {
            val allTasks = repository.getAllTasks()
            try {
                call.respond(HttpStatusCode.OK, allTasks)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }

        // GET a task by name
        get("/tasks/name/{name}") {
            val parameterName = call.parameters["name"]
            if (parameterName == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val task = repository.byName(parameterName)

                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respond(HttpStatusCode.OK, task)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }

        // GET task(s) by their priority
        get("/tasks/priority/{priority}") {
            val parameterPriority = call.parameters["priority"]
            if (parameterPriority == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val priority = Priority.valueOf(parameterPriority)
                val task = repository.byPriority(priority)
                if (task.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respond(HttpStatusCode.OK, task)

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }

        // POST a task ==> this endpoint isn't working
        post("/tasks") {
            try {
                val task = call.receive<Task>()

                if (task.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Name is required!")
                    return@post
                }
                if (task.description.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Description is required!")
                    return@post
                }

                repository.addTask(task)
                call.respond(HttpStatusCode.Created, task)

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }

        // DELETE a task
        delete("/tasks/{name}") {
            val name = call.parameters["name"]
            if (name == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            if (repository.removeTask(name)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
