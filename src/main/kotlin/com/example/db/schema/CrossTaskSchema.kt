package com.example.db.schema

import com.example.db.PlaylistService
import com.example.db.TaskService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class CrossTaskRef(
    private val database: Database
) {

    object TaskPlaylistCrossRef : Table("TaskPlaylistCrossRef") {
        val taskId: Column<Long> = long("TaskId").references(
            TaskService.Tasks.id,
            onDelete = ReferenceOption.CASCADE
        )
        val playlistId: Column<Long> = long("PlaylistId")
            .references(
                PlaylistService.Playlists.id,
                onDelete = ReferenceOption.CASCADE
            )
    }

    init {
        transaction(database) {

            SchemaUtils.create(TaskPlaylistCrossRef)
        }
    }

    fun addRef(taskId: Long, playlistId: Long) {
        transaction(database) {
            TaskPlaylistCrossRef.insert {
                it[TaskPlaylistCrossRef.taskId] = taskId
                it[TaskPlaylistCrossRef.playlistId] = playlistId
            }
        }
    }

}