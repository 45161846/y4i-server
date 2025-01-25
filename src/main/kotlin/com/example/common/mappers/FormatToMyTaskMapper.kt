package com.example.common.mappers

import com.example.common.Id
import com.example.common.TaskType
import com.example.common.entity.MyTask
import com.example.common.entity.PartOfTask

class FormatToMyTaskMapper {

    companion object {
        fun getDisplayableText(inputValue: String, topic: TaskType): String {

            return when (topic) {
                TaskType.NARECHIA -> narechiaText(inputValue)
                TaskType.PARONIM -> paronimText(inputValue)
                TaskType.YDARENIA -> ydarText(inputValue)
                TaskType.CLICKABLE -> clickableText(inputValue)
                else -> throw RuntimeException(
                    "Cannot get displayable text for word: $inputValue."
                            + "Of topic $topic"
                )
            }
        }

        private fun clickableText(input: String): String{
            val regex = "[\\[\\]]".toRegex()
            val parts = input.split(regex)

            val partsReplacedMyCharacters = List(parts.size){
                val part = parts[it]
                val p2 = part.split("|")
                if(p2.size == 1){
                    p2[0]
                }else{
                    p2.first { str ->
                        str.contains("*")
                    }.replace("*", "")
                }
            }
            return partsReplacedMyCharacters.joinToString(separator = "")
        }

        fun initialStringToWord(input: String, topic: TaskType): MyTask {
            return MyTask(
                value = input,
                type = topic
            )
        }

        fun wordToPartOfTask(word: MyTask, taskId: Id): List<PartOfTask> {

            return when (word.type) {

                TaskType.NARECHIA -> getAllNarechiaPartOfTasks(word, taskId)

                TaskType.PARONIM -> getAllParonimPartOfTasks(word, taskId)

                TaskType.YDARENIA -> getAllYdareniaPartOfTasks(word, taskId)

                TaskType.CLICKABLE -> getAllClickableParts(word, taskId)

            }

        }

        private fun getAllClickableParts(task: MyTask, taskId: Id): List<PartOfTask> {
            val regex = "[\\[\\]]".toRegex()
            return task.value.split(regex)
                .mapIndexed { ind, strPart ->
                    PartOfTask(taskId = taskId, value = strPart, index = ind, isCorrect = true)
                }

        }

        private fun getAllNarechiaPartOfTasks(word: MyTask, taskId: Id): List<PartOfTask> {
            val PartOfTaskStrData = word.value.split(";")[0]

            val PartOfTasksStr = PartOfTaskStrData.split("|")

            return List(PartOfTasksStr.size) {
                PartOfTask(
                    taskId = taskId,
                    value = PartOfTasksStr[it],
                    isCorrect = it == 0
                )
            }
        }

        private fun getAllParonimPartOfTasks(word: MyTask, taskId: Id): List<PartOfTask> {
            val parts = word.value.split(" - ")
            return List(parts.size) {
                PartOfTask(
                    taskId = taskId,
                    value = parts[it],
                    isCorrect = true
                )
            }
        }

        private fun getAllYdareniaPartOfTasks(word: MyTask, taskId: Id): List<PartOfTask> {
            return listOf(
                PartOfTask(
                    taskId = taskId,
                    value = word.value,
                    isCorrect = true
                )
            )
        }

        private fun narechiaText(input: String) = input.split("|")[0]

        private fun paronimText(input: String) = input
            .split(" - ")
            .joinToString(separator = " ") {
                it.split(" ")[0]
            }

        private fun ydarText(input: String) = input
    }
}