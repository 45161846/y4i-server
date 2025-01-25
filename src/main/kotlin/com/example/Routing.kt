package com.example

import com.example.wrappers.Credentials
import com.example.wrappers.RequestResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Pibadi pabadi pu!")
        }
    }
}
