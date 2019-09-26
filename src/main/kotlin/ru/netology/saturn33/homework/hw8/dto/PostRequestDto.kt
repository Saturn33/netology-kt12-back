package ru.netology.saturn33.homework.hw8.dto

import ru.netology.saturn33.homework.hw8.model.Location
import ru.netology.saturn33.homework.hw8.model.PostType

data class PostRequestDto(
    val id: Long,
    val author: String,
    val postType: PostType = PostType.POST,
    val content: String? = null,//for post, event, repost, youtube
    val location: Location? = null,//for event
    val video: String? = null//for youtube
)
