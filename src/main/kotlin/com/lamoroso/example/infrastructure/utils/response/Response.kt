package com.lamoroso.example.infrastructure.utils.response

import arrow.core.Either
import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.ktor.application.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

private val defaultDispatcher = Executors.newCachedThreadPool(
    ThreadFactoryBuilder().setNameFormat("default-routes-coroutine-dispatcher-%d").build()
).asCoroutineDispatcher()

suspend fun <E, R> ApplicationCall.runAsync(fn: () -> Either<E, R>): Either<E, R> {
    return withContext(defaultDispatcher) {
        fn()
    }
}

