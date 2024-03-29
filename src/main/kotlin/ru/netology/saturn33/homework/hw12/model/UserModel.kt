package ru.netology.saturn33.homework.hw12.model

import io.ktor.auth.Principal

data class UserModel(
    val id: Long = 0,
    val username: String,
    val password: String,
    val token: PushToken? = null
) : Principal
