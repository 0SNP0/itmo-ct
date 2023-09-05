package org.csc.kotlin2021.registry

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.csc.kotlin2021.UserAddress
import org.csc.kotlin2021.UserInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

fun Application.testModule() {
    (environment.config as MapApplicationConfig).apply {
        // define test environment here
    }
    module(testing = true)
}

class ApplicationTest {
    private val objectMapper = jacksonObjectMapper()
    private val testUserName = "pupkin"
    private val testHttpAddress = UserAddress("127.0.0.1", 9999)
    private val userData = UserInfo(testUserName, testHttpAddress)

    @BeforeEach
    fun clearRegistry() {
        Registry.users.clear()
    }

    @Test
    fun `health endpoint`() {
        withTestApplication({ testModule() }) {
            handleRequest(HttpMethod.Get, "/v1/health").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("OK", response.content)
            }
        }
    }

    @Test
    fun `register user`() = withRegisteredTestUser {
        handleRequest {
            method = HttpMethod.Post
            uri = "/v1/users"
            addHeader("Content-type", "application/json")
            setBody(objectMapper.writeValueAsString(userData))
        }.apply {
            assertEquals(HttpStatusCode.Conflict, response.status())
            val content = response.content ?: fail("No response content")
            assertEquals("User already registered", content)
        }

        handleRequest {
            method = HttpMethod.Post
            uri = "/v1/users"
            addHeader("Content-type", "application/json")
            setBody(objectMapper.writeValueAsString(UserInfo("!illegal", testHttpAddress)))
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            val content = response.content ?: fail("No response content")
            assertEquals("Illegal user name", content)
        }

        handleRequest {
            method = HttpMethod.Put
            uri = "/v1/users/$testUserName"
            addHeader("Content-type", "application/json")
            setBody(objectMapper.writeValueAsString(UserAddress("127.0.0.1", 9998)))
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val content = response.content ?: fail("No response content")
            val info = objectMapper.readValue<HashMap<String, String>>(content)

            assertNotNull(info["status"])
            assertEquals("ok", info["status"])
        }

        handleRequest {
            method = HttpMethod.Put
            uri = "/v1/users/!!!"
            addHeader("Content-type", "application/json")
            setBody(objectMapper.writeValueAsString(UserAddress("127.0.0.1", 9997)))
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            val content = response.content ?: fail("No response content")
            assertEquals("Illegal user name", content)
        }

        handleRequest {
            method = HttpMethod.Put
            uri = "/v1/users/user2"
            addHeader("Content-type", "application/json")
            setBody(objectMapper.writeValueAsString(UserAddress("127.0.0.1", 9996)))
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val content = response.content ?: fail("No response content")
            val info = objectMapper.readValue<HashMap<String, String>>(content)

            assertNotNull(info["status"])
            assertEquals("ok", info["status"])
        }
    }


    @Test
    fun `list users`() = withRegisteredTestUser {
        val users = hashMapOf(
            testUserName to hashMapOf("host" to testHttpAddress.host, "port" to testHttpAddress.port.toString())
        )
        handleRequest(HttpMethod.Get, "/v1/users").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val content = response.content ?: fail("No response content")
            val info = objectMapper.readValue<HashMap<String, HashMap<String, String>>>(content)
            assertEquals(users, info)
        }
    }

    @Test
    fun `delete user`() = withRegisteredTestUser {
        val delete = {
            handleRequest(HttpMethod.Delete, "/v1/users/$testUserName").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content ?: fail("No response content")
                val info = objectMapper.readValue<HashMap<String, String>>(content)

                assertNotNull(info["status"])
                assertEquals("ok", info["status"])
            }
        }
        delete()
        handleRequest(HttpMethod.Get, "/v1/users").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val content = response.content ?: fail("No response content")
            val info = objectMapper.readValue<HashMap<String, HashMap<String, String>>>(content)
            assertTrue(info.isEmpty())
        }
        delete()
    }

    private fun withRegisteredTestUser(block: TestApplicationEngine.() -> Unit) {
        withTestApplication({ testModule() }) {
            handleRequest {
                method = HttpMethod.Post
                uri = "/v1/users"
                addHeader("Content-type", "application/json")
                setBody(objectMapper.writeValueAsString(userData))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content ?: fail("No response content")
                val info = objectMapper.readValue<HashMap<String, String>>(content)

                assertNotNull(info["status"])
                assertEquals("ok", info["status"])

                this@withTestApplication.block()
            }
        }
    }
}
