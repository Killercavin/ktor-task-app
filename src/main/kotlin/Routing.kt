package com.example

import com.example.routes.taskRoute
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Ktor Task App")
        }

        // task routes
        taskRoute()
    }
}