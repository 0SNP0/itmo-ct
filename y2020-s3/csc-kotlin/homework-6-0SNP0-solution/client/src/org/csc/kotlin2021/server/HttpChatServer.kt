package org.csc.kotlin2021.server

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.csc.kotlin2021.Message
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

interface ChatMessageListener {
    fun messageReceived(userName: String, text: String)
}

class HttpChatServer(private val host: String, private val port: Int) {
    private val objectMapper = jacksonObjectMapper()
    private var listener: ChatMessageListener? = null

    private val engine = createEngine()

    private fun createEngine(): NettyApplicationEngine {
        val applicationEnvironment = applicationEngineEnvironment {
            log = LoggerFactory.getLogger("http-server")
            classLoader = ApplicationEngineEnvironment::class.java.classLoader
            connector {
                this.host = this@HttpChatServer.host
                this.port = this@HttpChatServer.port
            }
            module(configureModule())
        }
        return NettyApplicationEngine(applicationEnvironment)
    }

    fun start() {
        engine.start(true)
    }

    fun stop() {
        engine.stop(1000, 2000)
    }

    fun setMessageListener(listener: ChatMessageListener) {
        this.listener = listener
    }

    fun configureModule(): Application.() -> Unit = {
        install(CallLogging) {
            level = Level.DEBUG
            filter { call -> call.request.path().startsWith("/") }
        }

        install(DefaultHeaders) {
            header("X-Engine", "Ktor") // will send this header with each response
        }

        install(ContentNegotiation) {
            jackson() {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }

        routing {
            get("/v1/health") {
                call.respondText("OK", contentType = ContentType.Text.Plain)
            }

            post("/v1/message") {
                val (user, text) = call.receive<Message>()
                listener?.messageReceived(user, text)
                call.respond(mapOf("status" to "ok"))
            }

            install(StatusPages) {
                exception<IllegalArgumentException> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}

// Send test message using curl:
// curl -v -X POST http://localhost:8080/v1/message -H "Content-type: application/json" -d '{ "name":"ivanov", "text":"Hello!"}'
