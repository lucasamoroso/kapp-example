package com.lamoroso.example.domain.user

import arrow.core.Either
import arrow.core.flatMap
import com.lamoroso.example.infrastructure.repository.RepositoryError
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

object UserRoutes {

    private val log = KotlinLogging.logger {}

    val userRoutes =
        routes(
            "/users" bind routes(getUser(), getUsers(), createUser())
        )

    private fun getUsers(): RoutingHttpHandler =
        "/" bind Method.GET to {
            val users = emptyList<String>()
            log.info { "Listing all users" }
            Response(OK).body(Json.encodeToString(users))
        }

    private fun getUser(): RoutingHttpHandler =
        "/{id}" bind Method.GET to { req: Request ->
            Either.catch { req.path("id") }
                .mapLeft { throwable ->
                    log.error("Unexpected error reading query parameter. Reason ${throwable.message}", throwable)
                    InvalidUserError("The provided user id is invalid")
                }
                .flatMap { id -> UserService.getUser(id!!.toInt()) }
                .fold({ userError -> toErrorResponse(userError) },
                    { user -> Response(OK).body(Json.encodeToString(user)) }
                )

        }

    private fun createUser() =
        "/" bind Method.POST to { req: Request ->
            Either.catch { Json.decodeFromString<User>(req.bodyString()) }
                .mapLeft { throwable ->
                    log.error("Unexpected error serializing incoming user. Reason ${throwable.message}", throwable)
                    UserSerializationError(throwable.message)
                }
                .flatMap { user -> UserService.save(user) }
                .fold({ userError -> toErrorResponse(userError) },
                    { userId -> Response(OK).body(Json.encodeToString(userId)) }
                )
        }

    private fun toErrorResponse(userError: UserError): Response =
        when (userError) {
            is RepositoryError -> Response(INTERNAL_SERVER_ERROR).body(Json.encodeToString(userError))
            is UserSerializationError -> Response(BAD_REQUEST).body(Json.encodeToString(userError))
            is InvalidUserError -> Response(BAD_REQUEST).body(Json.encodeToString(userError))
            is UserNotFoundError -> Response(NOT_FOUND).body(Json.encodeToString(userError))
            else -> Response(INTERNAL_SERVER_ERROR).body(Json.encodeToString(userError))
        }

}