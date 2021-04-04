package com.lamoroso.example.domain.user

import arrow.core.Option
import arrow.core.getOrElse
import com.lamoroso.example.infrastructure.repository.RepositoryError
import com.lamoroso.example.infrastructure.utils.response.runAsync
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import mu.KotlinLogging

object UserRoutes {

    private val log = KotlinLogging.logger {}


    fun Application.registerUserRoutes() {
        routing {
            route("/users") {
                listUsersRoute()
                getUserRoute()
                createUserRoute()
            }
        }
    }

    private fun Route.listUsersRoute() {
        get("/") {
            call.runAsync { UserService.listUsers() }
                .fold({ userError -> toErrorResponse(userError) },
                    { users -> call.respond(users) }
                )
        }
    }

    private fun Route.getUserRoute() {
        get("/{id}") {
            Option
                .fromNullable(call.parameters["id"])
                .map { userId ->
                    //Run async, in another coroutine, then response
                    call
                        .runAsync { UserService.getUser(userId.toInt()) }
                        .fold({ userError -> toErrorResponse(userError) },
                            { user -> call.respond(user) }
                        )
                }
                .getOrElse { call.respond(BadRequest) }

        }
    }

    private fun Route.createUserRoute() {
        post("/") {
            val user = call.receive<User>()
            //Run async, in another coroutine, then response
            call
                .runAsync { UserService.save(user) }
                .fold({ userError -> toErrorResponse(userError) },
                    { userId -> call.respond(userId) }
                )
        }
    }


    private suspend fun PipelineContext<Unit, ApplicationCall>.toErrorResponse(
        userError: UserError
    ) {
        when (userError) {
            is RepositoryError -> call.respond(status = InternalServerError, userError)
            is InvalidUserError -> call.respond(status = BadRequest, userError)
            is UserNotFoundError -> call.respond(status = NotFound, userError)
            else -> call.respond(status = BadRequest, userError)
        }
    }

}