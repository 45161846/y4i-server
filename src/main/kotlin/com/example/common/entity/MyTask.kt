package com.example.common.entity

import com.example.common.Id
import com.example.common.TaskType
import kotlinx.serialization.Serializable

@Serializable
class PreviewTask(
    val value: String,
    val type: TaskType,
)

class FullTask(
    val preview: String,
    val formatedData: String,
    val type: TaskType,
)

class Answer(
    val userId: Id,
    val taskId: Id,
    val correct: Boolean,
    val solveTime: Time? = null
) {
    val time: Time = System.currentTimeMillis()
}


typealias Time = Long
