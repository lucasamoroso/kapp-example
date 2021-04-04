package com.lamoroso.example.domain.user

import arrow.core.Either
import arrow.core.flatten
import com.lamoroso.example.infrastructure.repository.MySqlRepository
import mu.KotlinLogging

object UserService {
    private val log = KotlinLogging.logger {}

    fun save(user: User): Either<UserError, Int> =
        user
            .asValidated()
            .fold({ msg ->
                log.info { "Invalid user: $msg" }
                Either.Left(InvalidUserError(msg))
            }, { validated ->
                log.info { "Creating new user ${validated.name}" }
                UserRepository.save(validated)
            })

    fun getUser(id: Int): Either<UserError, User> {
        log.info { "Looking for user $id" }
        return UserRepository
            .get(id)
            .map { users ->
                if (users.size == 1) Either.Right(users[0])
                else {
                    if (users.isEmpty()) Either.Left(UserNotFoundError("The user $id was not found"))
                    else Either.Left(UserDataCorruptionError("There is more than one user with id $id"))
                }
            }
            .flatten()
    }

    fun listUsers() : Either<UserError, List<User>> {
        log.info { "Listing all users" }
        return UserRepository.list()
    }


}