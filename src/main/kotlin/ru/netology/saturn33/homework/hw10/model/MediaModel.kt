package ru.netology.saturn33.homework.hw10.model

data class MediaModel(
    val id: String,
    val mediaType: MediaType
)

enum class MediaType {
    IMAGE
}
