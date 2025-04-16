package com.example.db

import com.example.common.Id
import com.example.common.TaskType
import com.example.common.entity.FullTask
import com.example.wrappers.PreviewTask
import com.example.db.schema.CrossTaskRef
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class TaskService(
    private val database: Database,
) {

    object Tasks : LongIdTable("Tasks") {
        val previewText = varchar("text", 512)
        val formatedData = varchar("formatedData", 512)
        val type: Column<TaskType> = enumeration("type", TaskType::class)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tasks)
        }
    }

    suspend fun addTask(task: FullTask): Long = dbQuery {
        Tasks.insert {
            it[type] = task.type
            it[previewText] = task.preview
            it[formatedData] = task.formatedData
        }
    }[Tasks.id].value


    suspend fun getPreviewTaskFromPlaylist(playlistId: Long): List<PreviewTask> = dbQuery {
        CrossTaskRef.TaskPlaylistCrossRef
            .innerJoin(
                Tasks,
                { taskId },
                { id }
            )
            .selectAll()
            .where {
                CrossTaskRef.TaskPlaylistCrossRef.playlistId eq playlistId
            }.map {
                PreviewTask(
                    Id(it[Tasks.id].value),
                    it[Tasks.previewText],
                )
            }

    }

    suspend fun getTaskFormatedStrings(requestedTaskId: Long): String? = dbQuery {
        Tasks
            .selectAll()
            .where {
                Tasks.id eq requestedTaskId
            }
            .map {
                it[Tasks.formatedData]
            }
            .firstOrNull()
    }
}

