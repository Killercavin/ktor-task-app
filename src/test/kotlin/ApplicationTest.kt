package com.example

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun applicationTest() = testApplication {
        application {
            module()
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Ktor Task App")
    }
}