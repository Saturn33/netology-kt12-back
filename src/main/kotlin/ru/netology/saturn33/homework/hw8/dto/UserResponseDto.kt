package ru.netology.saturn33.homework.hw8.dto

import ru.netology.saturn33.homework.hw8.model.UserModel

data class UserResponseDto(val id: Long, val username: String) {
    companion object {
        fun fromModel(model: UserModel) = UserResponseDto(
            id = model.id,
            username = model.username
        )
    }
}
