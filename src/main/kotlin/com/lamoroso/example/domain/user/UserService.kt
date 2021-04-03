package com.lamoroso.example.domain.user

import arrow.core.Either
import mu.KotlinLogging

object UserService {
    private val log = KotlinLogging.logger {}

    fun save(user: User): Either<UserError, User> {
        return user
            .asValidated()
            .fold({ msg ->
                log.info { "Invalid user: $msg" }
                Either.Left(InvalidUserError(msg))
            }, { validated ->
                log.info { "Creating new user ${validated.name}" }
                UserRepository.save(validated)
                    .mapLeft { throwable ->
                        log.error("Couldn't save user ${validated.name}. Reason ${throwable.message}", throwable)
                        UserPersistenceError(throwable.message)
                    }
            })

    }
}