package com.lamoroso.example.infrastructure.repository

import arrow.core.Either
import arrow.core.flatten
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.schema.BaseTable

object MySqlRepository {
    private val log = KotlinLogging.logger {}

    private val connection = buildDatasource().map { ds -> Database.connect(ds) }

    fun <T : BaseTable<*>> doInsert(table: T, block: AssignmentsBuilder.(T) -> Unit): Either<Throwable, Any> {
        return connection.map { db ->
            Either.catch {
                db.useTransaction {
                    log.info { "Inserting new record in table ${table.tableName}" }
                    db.insertAndGenerateKey(table, block)
                }
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