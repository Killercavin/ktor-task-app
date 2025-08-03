package com.example

import com.example.model.Priority
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {

    // GET all tasks
    @Test
    fun allTasksTest() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    // GET a task by name
    @Test
    fun byNameTest() = testApplication {
        application {
            module()
        }

        val name = "cleaning"
        val response = client.get("/tasks/name/$name")
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), name)
    }

    @Test
    fun nameNotFoundTest() = testApplication {
        application {
            module()
        }

        val name = "jogging"
        val response = client.get("/tasks/name/$name")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertContains(response.bodyAsText(), name)
    }

    @Test
    fun missingNameTest() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/name")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Missing task name parameter")
    }

    // GET task(s) by priority
    @Test
    fun priorityTest() = testApplication {
        application {
            module()
        }

        val priority = Priority.Medium
        val response = client.get("/tasks/priority/$priority")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun priorityMissingTest() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/priority")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Missing priority parameter")
    }

    @Test
    fun unUsedPriorityTest() = testApplication {
        application {
            module()
        }

        val priority = Priority.Vital
        val response = client.get("/tasks/priority/$priority")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertContains(response.bodyAsText(), "Task with priority '$priority' not found")
    }

    @Test
    fun invalidPriorityTest() = testApplication {
        application {
            module()
        }
        val priority = "Invalid"
        val response = client.get("/tasks/priority/$priority")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Invalid priority value, $priority")
    }

    // DELETE a task
    @Test
    fun deleteTaskTest() = testApplication {
        application {
            module()
        }

        val name = "cleaning"
        val response = client.delete("/tasks/delete/$name")
        assertEquals(HttpStatusCode.NoContent, response.status)
        assertContains(response.bodyAsText(), "")
    }

    @Test
    fun deleteTaskNameNotFound() = testApplication {
        application {
            module()
        }
        val name = "jogging"
        val response = client.delete("/tasks/delete/$name")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertContains(response.bodyAsText(), "Task with name '$name' not found")
    }

    @Test
    fun deleteTaskNameMissing() = testApplication {
        application {
            module()
        }

        val response = client.delete("/tasks/delete")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Missing task name parameter")
    }

    // POST a task
    @Test
    fun addTask() = testApplication {
        application {
            module()
        }
        val body = """
            {
                "name": "test",
                "description": "Test task description",
                "priority": "High"
            }
        """.trimIndent()

        val response = client.post("/tasks") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(body)
            // setBody(Json.encodeToString(Task("test", "Test task", Priority.High)))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertContains(response.bodyAsText(), "test")
    }

    @Test
    fun addTaskWithMissingNameTest() = testApplication {
        application { module() }

        val body = """
        {
            "name": "",
            "description": "Something useful",
            "priority": "High"
        }
    """.trimIndent()

        val response = client.post("/tasks") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(body)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Name is required")
    }

    @Test
    fun addTaskWithMissingDescriptionTest() = testApplication {
        application { module() }

        val body = """
        {
            "name": "Read",
            "description": "",
            "priority": "High"
        }
    """.trimIndent()

        val response = client.post("/tasks") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(body)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Description is required")
    }

    @Test
    fun addTaskWithInvalidPriorityTest() = testApplication {
        application { module() }

        val body = """
        {
            "name": "Task1",
            "description": "Desc",
            "priority": "Extreme"  // Not part of the priority enum
        }
    """.trimIndent()

        val response = client.post("/tasks") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(body)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "")
    }
}