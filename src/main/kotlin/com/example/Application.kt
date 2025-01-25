package com.example

import com.example.db.configureDatabases
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    encodeDefaults = true
}

fun main(args: Array<String>) {

    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    configureSerialization()

//    configureRouting()
    configureDatabases()
    install(ContentNegotiation){
        json(json)
    }
}
