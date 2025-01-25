package com.example.db.schema

import com.example.db.dbQuery
import com.example.wrappers.PreviewTask
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PreviewTaskSchema(
    private val database: Database,
) {

    object PreviewTaskTable : LongIdTable("PreviewTask") {

        val playlistId = long("playlistId")
        val previewTask = varchar("preview_task", 80)

    }

    init {
        transaction(database) {
            SchemaUtils.create(PreviewTaskTable)
        }
    }

    suspend fun addPreviewTask(task: String, playlistId: Long) {
        transaction {
            PreviewTaskTable.insert {
                it[this.previewTask] = task
                it[this.playlistId] = playlistId
            }
        }
    }

    suspend fun getPreviewTask(id: Long): List<PreviewTask> = dbQuery {
        PreviewTaskTable
            .selectAll()
            .where {
                PreviewTaskTable.playlistId eq id
            }.map {
                PreviewTask(
                    id = id,
                    text = it[PreviewTaskTable.previewTask]
                )
            }
    }

}