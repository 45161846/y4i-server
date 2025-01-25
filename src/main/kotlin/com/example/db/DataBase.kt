package com.example.db

import com.example.common.loading.PlaylistLoader
import com.example.db.TaskService.Tasks
import com.example.db.schema.PreviewTaskSchema
import com.example.wrappers.Credentials
import com.example.wrappers.RequestResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:file:D:\\Progs\\IntelliJ IDEA 2024.3\\Projects\\y4i-server\\src\\main\\kotlin\\com\\example\\db\\first_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )
    transaction {
        Tasks.deleteAll()
        PlaylistService.Playlists.deleteAll()
        PreviewTaskSchema.PreviewTaskTable.deleteAll()
    }


    val userService = UserService(database)
    val playlistService = PlaylistService(database)
    val taskService = TaskService(database)

    MainScope().launch(Dispatchers.IO) {
        val baseName = "D:\\Progs\\IntelliJ IDEA 2024.3\\Projects\\y4i-server\\src\\main\\assets"
        val loader = PlaylistLoader(baseName)
        val names = loader.filesToAdd()
        names
            .map {
                baseName + "\\" + it
            }
            .forEach {
                val playlist = loader.createPlaylistFromFile(it)
                playlistService.addPlaylist(playlist)
                val tasks = loader.createTasksFromFile(it)
                tasks.forEach { task ->
                    taskService.addTask(task)
                }
            }
    }

    routing {
        // Create user
        post("/user/register") {
            val credentials = call.receive<Credentials>()
            when (credentials) {
                is Credentials.Valid -> {

                    val id = userService.create(credentials)
                    val response: RequestResult = RequestResult.CreateAccount.Created(
                        "wquiey1782uhew"
                    )
                    call.respond(HttpStatusCode.Created, response)
                }

                is Credentials.Empty -> call.respond(HttpStatusCode.BadRequest)
            }

        }

        post("/user/login") {

            val result: Credentials = call.receive<Credentials>()

            when (result) {
                is Credentials.Empty -> call.respond(HttpStatusCode.BadRequest)
                is Credentials.Valid -> {

                    val user = userService.findByEmail(result.email)

                    if (user != null) {
                        val requestResult: RequestResult = RequestResult.Authentication.Granted
                        call.respond(HttpStatusCode.OK, requestResult)
                    } else {
                        val requestResult: RequestResult =
                            RequestResult.Authentication.Denied("Invalid login or password")
                        call.respond(
                            HttpStatusCode.OK, requestResult
                        )
                    }
                }
            }
        }

        get("/playlist/all") {
            val playlists = playlistService.takeAll()

            call.respond(HttpStatusCode.OK, playlists)

        }

        get("/playlist") {
            val after = call.parameters["after"]?.toInt()
            val amount = call.parameters["amount"]?.toInt()

            if (after != null && amount != null) {
                call.respond(
                    playlistService.takeAfter(after, amount)
                )
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
