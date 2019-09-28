package ru.netology.saturn33.homework.hw8.model

import io.ktor.auth.Principal

data class UserModel(
    val id: Long = 0,
    val username: String,
    val password: String
) : Principal
