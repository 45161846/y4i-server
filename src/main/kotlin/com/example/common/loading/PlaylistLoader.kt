package com.example.common.loading

import com.example.common.Id
import com.example.common.TaskType
import com.example.common.entity.MyTask
import com.example.common.entity.Playlist
import com.example.wrappers.RemotePlaylist
import java.io.File

class PlaylistLoader(
    private val checkerPath: String,
) {

    fun filesToAdd(): List<String> {
        val files = File(checkerPath).listFiles()
        val names = files?.map {
            it.name
        }
        return names ?: emptyList()
    }

    fun createPlaylistFromFile(fileName: String): RemotePlaylist {

        val lines = File(fileName).readLines()
        val parts = lines[0].split(";")

        val preview = lines[1].split(";")

        return RemotePlaylist(
            remoteId = Id(-1),
            title = parts[0],
            description = parts[1],
            rating = parts[2].toFloat(),
            capacity = parts[3].toInt(),
            previewTasks = preview.subList(0, 3.coerceAtMost(preview.size))
        )

    }

    fun createTasksFromFile(fileName: String): List<MyTask> {
        val lines = File(fileName).readLines()
        val taskLines = lines.subList(2, lines.size)

        return taskLines
            .filter {
                it.isNotEmpty()
            }
            .map {
            val parts = it.split(";")
            MyTask(
                parts[0], TaskType.valueOf(parts[1])
            )
        }
    }

}