package com.lamoroso.example.server

import com.lamoroso.example.domain.user.UserRoutes
import io.opentelemetry.context.propagation.ContextPropagators.create
import io.opentelemetry.extension.aws.AwsXrayPropagator
import io.opentelemetry.sdk.OpenTelemetrySdk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import mu.KotlinLogging
import org.http4k.core.then
import org.http4k.filter.OpenTelemetryTracing
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Netty
import org.http4k.server.asServer


object Server {

    private val openTelemetry = OpenTelemetrySdk.builder()
        .setPropagators(create(AwsXrayPropagator.getInstance()))
        .buildAndRegisterGlobal()

    private val log = KotlinLogging.logger {}

    private val app: RoutingHttpHandler =
        ServerFilters.OpenTelemetryTracing(openTelemetry)
            .then(
                routes(
                    "/kapp" bind   UserRoutes.userRoutes
                )
            )

    fun start(): Http4kServer = run {
        app.asServer(Netty(8080)).start().also { server -> log.info { "Server started at port ${server.port()}" } }
    }
}