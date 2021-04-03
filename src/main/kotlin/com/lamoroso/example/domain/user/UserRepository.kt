package com.lamoroso.example.domain.user

import arrow.core.Either
import com.lamoroso.example.infrastructure.repository.MySqlRepository


interface RepositoryAlgebra<T> {
    fun save(user: T): Either<Throwable, T>
}

object UserRepository : RepositoryAlgebra<User> {

    override fun save(user: User): Either<Throwable, User> =
            MySqlRepository.doInsert(UserTable){
                set(it.age, user.age)
                set(it.name, user.name)
                set(it.lastName, user.lastName)
            }.map { user }


}