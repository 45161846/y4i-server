package com.example.db

import com.example.common.Id
import com.example.db.PlaylistService.Playlists.select
import com.example.db.PlaylistService.Playlists.title
import com.example.db.schema.PreviewTaskSchema
import com.example.db.schema.PreviewTaskSchema.PreviewTaskTable.playlistId
import com.example.db.schema.PreviewTaskSchema.PreviewTaskTable.previewTask
import com.example.wrappers.PreviewTask
import com.example.wrappers.RemotePlaylist
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class PlaylistService(
    private val database: Database,
) {

    object Playlists : LongIdTable("Playlists") {
        val title = varchar("title", 128)
        val description = varchar("description", 300)
        val rating = float("rating")
        val capacity = integer("capacity")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Playlists)
        }
    }

    suspend fun addPlaylist(playlist: RemotePlaylist): Long =
        dbQuery {

            val previewService = PreviewTaskSchema(database)

            val id = Playlists.insert {
                it[title] = playlist.title
                it[description] = playlist.description
                it[rating] = playlist.rating
                it[capacity] = playlist.capacity
            }[Playlists.id].value

            for (preview in playlist.previewTasks) {
                if(preview.isBlank()){
                    continue
                }
                previewService.addPreviewTask(preview, id)
            }

            id
        }

    suspend fun takeAll(): List<RemotePlaylist> =
        dbQuery {

            val previewService = PreviewTaskSchema(database)

            val emptyPlaylists = Playlists
                .selectAll()

                .map {
                    val id = it[Playlists.id].value

                    val playlist = RemotePlaylist(
                        remoteId = Id(id),
                        title = it[title],
                        description = it[Playlists.description],
                        rating = it[Playlists.rating],
                        capacity = it[Playlists.capacity],
                        emptyList()
                    )

                    val preview = previewService.getPreviewTask(id)

                    playlist.copy(
                        previewTasks = preview.map { it.previewText }
                    )
                }

            val previewTasks = PreviewTaskSchema.PreviewTaskTable
                .selectAll()
                .map{
                    PreviewTask(
                        Id(it[PreviewTaskSchema.PreviewTaskTable.id].value),
                        it[previewTask]
                    )
                }
                .groupBy{
                    it.remoteId
                }


            emptyPlaylists.map{
                it.copy(
                    previewTasks = previewTasks[it.remoteId]?.map { task ->
                        task.previewText
                    } ?: emptyList()
                )
            }
        }

    suspend fun takeAfter(after: Int, amount: Int): List<RemotePlaylist> =
        dbQuery {

            val previewService = PreviewTaskSchema(database)

            Playlists
                .selectAll()
                .limit(amount)
                .offset(after.toLong())
                .map {
                    rowToPlaylist(it, previewService)
                }
        }

    private suspend fun rowToPlaylist(
        row: ResultRow,
        previewService: PreviewTaskSchema
    ): RemotePlaylist {
        val id = row[Playlists.id].value

        val playlist = RemotePlaylist(
            remoteId = Id(id),
            title = row[title],
            description = row[Playlists.description],
            rating = row[Playlists.rating],
            capacity = row[Playlists.capacity],
            emptyList()
        )

        val preview = previewService.getPreviewTask(id)

        return playlist.copy(
            previewTasks = preview.map { it.previewText }
        )
    }
}