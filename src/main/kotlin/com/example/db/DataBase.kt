package com.example.db

import com.example.common.loading.PlaylistLoader
import com.example.db.TaskService.Tasks
import com.example.db.schema.CrossTaskRef
import com.example.db.schema.PreviewTaskSchema
import com.example.wrappers.Credentials
import com.example.wrappers.RemotePlaylist
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
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

const val CACHE_PATH = "D:\\Progs\\IntelliJ IDEA 2024.3\\Projects\\y4i-server\\src\\main\\cache"

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:file:D:\\Progs\\IntelliJ IDEA 2024.3\\Projects\\y4i-server\\src\\main\\kotlin\\com\\example\\db\\main_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

//    transaction {
//        CrossTaskRef.TaskPlaylistCrossRef.deleteAll()
//        Tasks.deleteAll()
//        PlaylistService.Playlists.deleteAll()
//        PreviewTaskSchema.PreviewTaskTable.deleteAll()
//    }


    val userService = UserService(database)
    val playlistService = PlaylistService(database)
    val taskService = TaskService(database)
    val crossService = CrossTaskRef(database)

//    MainScope().launch(Dispatchers.IO) {
//        val baseName = "D:\\Progs\\IntelliJ IDEA 2024.3\\Projects\\y4i-server\\src\\main\\assets"
//        val loader = PlaylistLoader(baseName)
//        val names = loader.filesToAdd()
//        names.map {
//            baseName + "\\" + it
//        }.forEach {
//            val playlist = loader.createPlaylistFromFile(it)
//            val playlistId = playlistService.addPlaylist(playlist)
//            val tasks = loader.createTasksFromFile(it)
//            tasks.forEach { task ->
//                val taskId = taskService.addTask(task)
//                crossService.addRef(taskId, playlistId)
//            }
//        }
//    }



    routing {

        get("/") {
            call.respondText("HELLO WORLD!", ContentType.Text.Plain)
        }

        // Create user
        post("/user/register") {
            val credentials = call.receive<Credentials>()
            when (credentials) {
                is Credentials.Valid -> {

                    val id = userService.create(credentials)
                    val response: RequestResult = RequestResult.CreateAccount.Created(
                        UUID.randomUUID().toString()
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

            playlistResponse(playlists, call)

        }

        get("/playlist") {
            val after = call.parameters["after"]?.toInt()
            val amount = call.parameters["amount"]?.toInt()

            if (after != null && amount != null) {
                playlistResponse(
                    playlistService.takeAfter(after, amount), call
                )
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/playlist/{id}") {
            val id = call.parameters["id"]?.toLong()

            id?.let { id ->
                val tasks = taskService.getPreviewTaskFromPlaylist(id)

                if (tasks.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, RequestResult.RemoteTasks.Tasks(tasks))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }

            } ?: call.respond(HttpStatusCode.BadRequest)
        }

        get("download/task") {
            val id = call.parameters["id"]?.toLong()
            id?.let {

                val string = taskService.getTaskFormatedStrings(id)

                if (string != null) {
                    call.respond(HttpStatusCode.OK, RequestResult.TaskDownload.Data(string))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}

suspend fun playlistResponse(playlists: List<RemotePlaylist>, call: RoutingCall) {

    val data = if (playlists.isNotEmpty()) {
        RequestResult.PlaylistPage.Data(playlists)
    } else {
        RequestResult.PlaylistPage.NotingMore
    }

    call.respond(HttpStatusCode.OK, data)
}