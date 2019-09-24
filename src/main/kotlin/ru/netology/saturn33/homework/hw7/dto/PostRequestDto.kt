package ru.netology.saturn33.homework.hw7.dto

import ru.netology.saturn33.homework.hw7.model.Location
import ru.netology.saturn33.homework.hw7.model.PostType

data class PostRequestDto(
    val id: Long,
    val author: String,
    val postType: PostType = PostType.POST,
    val content: String? = null,//for post, event, repost, youtube
    val location: Location? = null,//for event
    val source: Long? = null,//for repost
    val video: String? = null//for youtube
)
