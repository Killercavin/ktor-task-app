package com.example

import com.example.model.Priority
import com.example.model.TaskRepository
import com.example.model.tasksAsTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText { "Hello World" }
        }

        // GET all tasks
        get("/tasks") {
            val tasks = TaskRepository.allTasks()
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )

            // GET tasks by their name
            get("/name/{name}") {
                val name = call.parameters["name"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    TaskRepository.taskByName(name)
                    call.respond(HttpStatusCode.OK)
                    call.respondText(
                        contentType = ContentType.parse("text/html"),
                        text = """
                            <p>Task Name: $name</p>
                        """.trimIndent()
                    )
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest, e)
                }
            }

            // GET task(s) by their priority
            get("/priority/{priority}") {
                val priorityAsText = call.parameters["priority"]

                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = TaskRepository.tasksByPriority(priority)

                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }

                    call.respondText(
                        contentType = ContentType.parse("text/html"),
                        text = tasks.tasksAsTable()
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
