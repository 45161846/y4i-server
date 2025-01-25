package com.example.db

import com.example.wrappers.Credentials
import jakarta.annotation.Priority
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class PostgresUserRepository : UserRepository {
    override suspend fun allTasks(): List<Credentials>  = dbQuery{
        UserDAO.all().map(::daoToModel)
    }

    override suspend fun tasksByLogin(login: String): List<Credentials> = dbQuery{
        UserDAO
            .find { UserService.Users.login eq login }
            .map(::daoToModel)
    }

    override suspend fun taskByEmail(email: String): Credentials? = dbQuery{
        UserDAO
            .find { (UserService.Users.email eq email) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addTask(task: Credentials) {
        TODO("Not yet implemented")
    }

    override suspend fun removeTask(name: String): Boolean {
        TODO("Not yet implemented")
    }
}

interface UserRepository {
    suspend fun allTasks(): List<Credentials>
    suspend fun tasksByLogin(login: String): List<Credentials>
    suspend fun taskByEmail(email: String): Credentials?
    suspend fun addTask(task: Credentials)
    suspend fun removeTask(name: String): Boolean
}