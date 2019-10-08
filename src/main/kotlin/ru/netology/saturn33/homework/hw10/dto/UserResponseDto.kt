package ru.netology.saturn33.homework.hw10.dto

import ru.netology.saturn33.homework.hw10.model.UserModel

data class UserResponseDto(val id: Long, val username: String) {
    companion object {
        fun fromModel(model: UserModel) = UserResponseDto(
            id = model.id,
            username = model.username
        )
    }
}
