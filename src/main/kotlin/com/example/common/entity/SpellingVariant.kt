package com.example.common.entity

import com.example.common.Id

data class SpellingVariant(

    var id: Id = Id(0L),

    val partOfTaskId: Id = Id(0L),
    val value: String,
    val correct: Boolean
)

data class PartOfTaskWithSpellingVariants(
    val partOfTask: PartOfTask,

    val spellings: List<SpellingVariant>
)