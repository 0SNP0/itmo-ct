package org.csc.kotlin2021.registry

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import org.csc.kotlin2021.UserAddress
import org.csc.kotlin2021.UserInfo
import org.csc.kotlin2021.checkUserName
import org.slf4j.event.Level
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    thread {
        // TODO: periodically check users and remove unreachable ones
    }
    EngineMain.main(args)
}

object Registry {
    val users = ConcurrentHashMap<String, UserAddress>()
}

@Suppress("UNUSED_PARAMETER")
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(StatusPages) {
        exception<IllegalArgumentException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "invalid argument")
        }
        exception<UserAlreadyRegisteredException> { cause ->
            call.respond(HttpStatusCode.Conflict, cause.message ?: "user already registered")
        }
        exception<IllegalUserNameException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "illegal user name")
        }
    }
    routing {
        get("/v1/health") {
            call.respondText("OK", contentType = ContentType.Text.Plain)
        }

        post("/v1/users") {
            val user = call.receive<UserInfo>()
            val name = user.name
            checkUserName(name) ?: throw IllegalUserNameException()
            if (Registry.users.containsKey(name)) {
                throw UserAlreadyRegisteredException()
            }
            Registry.users[name] = user.address
            respondOk()
        }

        get("/v1/users") {
            call.respond(Registry.users.mapValues {
                mapOf(
                    "host" to it.value.host,
                    "port" to it.value.port
                )
            })
        }

        put("/v1/users/{name}") {
            val name = getName()
            checkUserName(name) ?: throw IllegalUserNameException()
            val address = call.receive<UserAddress>()
            Registry.users[name] = address
            respondOk()
        }

        delete("/v1/users/{name}") {
            Registry.users.remove(getName())
            respondOk()
        }
    }
}

suspend fun PipelineContext<*, ApplicationCall>.respondOk() =
    call.respond(mapOf("status" to "ok"))

fun PipelineContext<*, ApplicationCall>.getName() =
    call.parameters["name"] ?: throw IllegalUserNameException()

class UserAlreadyRegisteredException : RuntimeException("User already registered")
class IllegalUserNameException : RuntimeException("Illegal user name")
