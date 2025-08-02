package com.example

import com.example.model.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val taskRepository = TaskRepository()

    routing {
        staticResources("/", "static")

        // GET tasks
        get("/tasks") {
            val allTasks = taskRepository.getAllTasks()
            call.respond(HttpStatusCode.OK, allTasks.joinToString("\n"))
        }
    }
}
