package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

private val logger = LoggerFactory.getLogger("ApplicationTest")

class ApplicationTest {

    // GET all tasks
    @Test
    fun allTasksTest() = testApplication {
        application {
            module()
            configureRouting()
        }

        val response = client.get("/tasks")
        val body = response.bodyAsText()

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(body, body)
        logger.info(body)
    }

    // GET tasks by priority
    @Test
    fun taskByPriorityTest() = testApplication {
        application {
            module()
            configureRouting()
        }

        val response = client.get("/tasks/priority/Low")
        val body = response.bodyAsText()

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(body, "Clean the house")
    }
}