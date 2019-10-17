package ru.netology.saturn33.homework.hw12.dto

import ru.netology.saturn33.homework.hw12.model.UserModel

data class UserResponseDto(val id: Long, val username: String) {
    companion object {
        fun fromModel(model: UserModel) = UserResponseDto(
            id = model.id,
            username = model.username
        )
    }
}
