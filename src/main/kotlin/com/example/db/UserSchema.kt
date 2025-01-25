package com.example.db

import com.example.db.UserService.Users
import com.example.wrappers.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(Users)

    var login by Users.login
    var email by Users.email
    var password by Users.password
}

fun daoToModel(dao: UserDAO) = Credentials.Valid(
    dao.login,
    dao.email,
    dao.password
)

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { addLogger(StdOutSqlLogger);block() }

class UserService(
    private val database: Database,
) {
    object Users : IntIdTable("created-users") {
        val login = varchar("user-name", length = 18)
        val email = varchar("user-email", length = 100)
        val password = varchar("password", length = 30)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }


    suspend fun create(credentials: Credentials.Valid): Int =
        dbQuery {
            Users.insert {
                it[login] = credentials.login
                it[email] = credentials.email
                it[password] = credentials.password
            }[Users.id]
        }.value

    suspend fun findByEmail(email: String): Credentials? =
        dbQuery {
            Users
                .selectAll()
                .where { Users.email eq email }
                .map { Credentials.Valid(
                    it[Users.login],
                    it[Users.email],
                    it[Users.password]
                ) }
                .singleOrNull()
        }

    suspend fun update(
        id: Int,
        credentials: Credentials.Valid,
    ) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[login] = credentials.login
                it[email] = credentials.email
                it[password] = credentials.password
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}
