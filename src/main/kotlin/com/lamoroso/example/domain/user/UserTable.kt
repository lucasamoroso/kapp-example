package com.lamoroso.example.domain.user

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserTable: Table<Nothing>("user") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val lastName = varchar("last_name")
    val age = int("age")
}