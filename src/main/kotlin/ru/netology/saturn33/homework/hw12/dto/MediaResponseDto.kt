package ru.netology.saturn33.homework.hw12.dto

import ru.netology.saturn33.homework.hw12.model.MediaModel
import ru.netology.saturn33.homework.hw12.model.MediaType

data class MediaResponseDto(val id: String, val mediaType: MediaType) {
    companion object {
        fun fromModel(model: MediaModel) = MediaResponseDto(
            id = model.id,
            mediaType = model.mediaType
        )
    }
}
