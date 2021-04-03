package com.lamoroso.example.domain.user

import com.lamoroso.example.infrastructure.repository.RepositoryError
import kotlinx.serialization.Serializable

interface UserError

@Serializable data class UserSerializationError(val message: String?) : UserError
@Serializable data class InvalidUserError(val message: String) : UserError
@Serializable data class UserNotFoundError(val message: String = "User not found") : UserError


@Serializable data class UserPersistenceError(val message: String?) : UserError, RepositoryError
@Serializable data class UserRepositoryError(val message: String?) : UserError, RepositoryError
@Serializable data class UserDataCorruptionError(val message: String = "There is more users that expected") : UserError, RepositoryError