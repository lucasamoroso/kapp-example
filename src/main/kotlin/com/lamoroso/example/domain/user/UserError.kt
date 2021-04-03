package com.lamoroso.example.domain.user

import kotlinx.serialization.Serializable

interface UserError

@Serializable data class UserSerializationError(val message: String?) : UserError
@Serializable data class InvalidUserError(val message: String) : UserError
@Serializable data class UserPersistenceError(val message: String?) : UserError