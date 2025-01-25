package com.example.common.entity

import com.example.common.Id
import com.example.common.TaskType

class MyTask(
    val value: String,
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
