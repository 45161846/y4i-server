package com.example.common.loading

import com.example.common.Id
import com.example.common.TaskType
import com.example.common.entity.MyTask
import com.example.common.entity.PartOfTask
import com.example.common.entity.SpellingVariant
import com.example.common.mappers.FormatToMyTaskMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FilleReading {

    private suspend fun readFile(fileName: String): List<String> {
        return withContext(Dispatchers.IO) {
            val file = File(fileName)

            file.readLines()
        }
    }

    suspend fun getAllWords(fromFile: String): List<MyTask> {
        val rows = readFile(fromFile)

        val infos = childrenFilesInfo(rows)

        return getAllInitialWords(infos)
    }

    suspend fun getAllPartOfTasksToDBWords(words: List<MyTask>, taskId: List<Id>): List<PartOfTask> {
        return List(words.size) {
            FormatToMyTaskMapper.wordToPartOfTask(words[it], taskId[it])
        }.flatten()
    }

    fun contextWord(inputValue: String): String {
        val parts = inputValue.split(";")

        if (parts.size < 2) return ""

        return parts.last()
    }

    private suspend fun getAllInitialWords(infos: List<ChildFileInfo>): List<MyTask> {

        return List(infos.size) {
            getWordsByFileInfo(infos[it])
        }.flatten()

    }

    private fun childrenFilesInfo(values: List<String>): List<ChildFileInfo> {
        return List(values.size) {
            ChildFileInfo(values[it])
        }
    }

    private suspend fun getWordsByFileInfo(info: ChildFileInfo): List<MyTask> {
        val rows = readFile(info.childFileName)

        val mapper = FormatToMyTaskMapper

        return List(rows.size) {
            mapper.initialStringToWord(rows[it], info.topic)
        }
    }


    private class ChildFileInfo(
        value: String
    ) {

        val childFileName: String
        val topic: TaskType
        val rowsCount: Int


        init {
            val splitValues = value.split(";")
            childFileName = splitValues[0]
            topic = TaskType.valueOf(splitValues[1])
            rowsCount = Integer.parseInt(splitValues[2])
        }

    }

}