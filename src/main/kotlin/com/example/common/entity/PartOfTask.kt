package com.example.common.entity

import com.example.common.Id


data class PartOfTask(

    var taskId: Id,

    var value: String,

    var index: Int = 0,

    var isCorrect: Boolean
)
