package com.lamoroso.example.infrastructure.repository

import arrow.core.Either
import arrow.core.flatten
import com.lamoroso.example.domain.user.User
import com.lamoroso.example.domain.user.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.BaseTable
import org.ktorm.schema.ColumnDeclaring

object MySqlRepository {
    private val log = KotlinLogging.logger {}

    private val connection = buildDatasource().map { ds -> Database.connect(ds) }

    fun <T : BaseTable<*>, R> doInsertAndReturnKey(table: T, block: AssignmentsBuilder.(T) -> Unit): Either<Throwable, R> {
        return connection.map { db ->
            Either.catch {
                db.useTransaction {
                    log.info { "Inserting new record in table ${table.tableName}" }
                    db.insertAndGenerateKey(table, block) as R
                }
            }
        }.flatten()
    }

    fun <T : BaseTable<*>> doSelect(table: T, condition: ColumnDeclaring<Boolean>): Either<Throwable, Query> {
        return connection.map { db ->
            Either.catch {
                    log.debug { "Executing query in ${table.tableName} with condition ${condition.sqlType.toString()}" }
                    val a = db.from(table).select().where(condition)
                a
            }
        }.flatten()
    }

    private fun buildDatasource(): Either<Throwable, HikariDataSource> {
        return Either.catch {
            Class.forName("com.mysql.cj.jdbc.Driver")
            val jdbcConfig = HikariConfig()
            jdbcConfig.poolName = "example"
            jdbcConfig.jdbcUrl = "jdbc:mysql://localhost:3306/kapp"
            jdbcConfig.username = "local"
            jdbcConfig.password = "test"
            HikariDataSource(jdbcConfig)
        }
    }
}