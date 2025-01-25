package com.example.db

import com.example.common.TaskType
import com.example.common.entity.MyTask
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class TaskService(
    private val database: Database,
){

    object Tasks : LongIdTable("Tasks") {
        val value = varchar("text", 512)
        val type: Column<TaskType> = enumeration("type", TaskType::class)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tasks)
        }
    }

    suspend fun addTask(task: MyTask) {
        transaction(database) {
            Tasks.insert {
                it[type] = task.type
                it[value] = task.value
            }
        }
    }

}