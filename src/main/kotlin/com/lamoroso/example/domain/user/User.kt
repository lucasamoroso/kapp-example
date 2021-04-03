package com.lamoroso.example.domain.user

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import arrow.core.zip
import arrow.typeclasses.Semigroup
import kotlinx.serialization.Serializable

@Serializable
data class User(val name: String, val lastName: String, val age: Int) {
    private fun ageRange(): Validated<String, Int> =
        if (age > 120 || age < 0)
            "Age must be between 0 and 120.".invalid()
        else age.valid()

    private fun nameLengthRange(): Validated<String, String> =
        if (name.length > 55 || name.length < 2)
            "Name must have 2 to 55 characters.".invalid()
        else name.valid()

    private fun lastNameLengthRange(): Validated<String, String> =
        if (lastName.length > 55 || lastName.length < 2)
            "LastName must have 2 to 55 characters.".invalid()
        else lastName.valid()

    fun asValidated(): Validated<String, User> =
        ageRange().zip(Semigroup.string(), nameLengthRange(), lastNameLengthRange())
        { age, name, lastName -> User(name, lastName, age) }

}





