package com.example

import com.example.wrappers.RequestResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Pibadi pabadi pu!")
        }

        get("/user/{login}") {

            val result = RequestResult.Authentication.Denied("Invalid login or password")

            call.respond(
                message = result,
                status = HttpStatusCode.OK
            )
        }

    }
}
