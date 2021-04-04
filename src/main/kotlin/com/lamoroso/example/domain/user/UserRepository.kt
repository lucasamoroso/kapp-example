package com.lamoroso.example.domain.user

import arrow.core.Either
import com.lamoroso.example.infrastructure.repository.MySqlRepository
import mu.KotlinLogging
import org.ktorm.dsl.Query
import org.ktorm.dsl.eq
import org.ktorm.dsl.map


interface RepositoryAlgebra<E, T> {
    fun <I> save(user: T): Either<E, I>
    fun get(id: Int): Either<E, List<T>>
    fun list(): Either<E, List<T>>
}

object UserRepository : RepositoryAlgebra<UserError, User> {

    private val log = KotlinLogging.logger {}

    override fun <Int> save(user: User): Either<UserError, Int> =
        MySqlRepository.doInsertAndReturnKey<UserTable, Int>(UserTable) {
            set(it.age, user.age)
            set(it.name, user.name)
            set(it.lastName, user.lastName)
        }.mapLeft { throwable ->
            log.error("Unexpected exception saving user $user", throwable)
            UserPersistenceError(throwable.message)
        }

    override fun get(id: Int): Either<UserError, List<User>> =
        MySqlRepository
            .doSelect(UserTable, UserTable.id.eq(id))
            .map()

    override fun list(): Either<UserError, List<User>> =
        MySqlRepository
            .doSelect(UserTable)
            .map()

    /**
     * Map q query execution into an Either<UserRepositoryError, List<User>>
     */
    private fun Either<Throwable, Query>.map(): Either<UserRepositoryError, List<User>> =
        this.map { query ->
            query.map { row ->
                User(row[UserTable.name]!!, row[UserTable.lastName]!!, row[UserTable.age]!!)
            }
        }.mapLeft { throwable ->
            log.error("Unexpected exception executing query in table users", throwable)
            UserRepositoryError(throwable.message)
        }


}