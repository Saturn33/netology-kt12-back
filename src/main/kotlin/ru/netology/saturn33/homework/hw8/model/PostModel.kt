package ru.netology.saturn33.homework.hw8.model

import ru.netology.saturn33.homework.hw8.dto.PostResponseDto
import java.util.*

data class PostModel(
    val id: Long,
    val created: Date = Date(),
    val author: Long,
    val likes: Set<Long> = setOf(),
    val views: Int = 0,
    val shares: Int = 0,
    val postType: PostType = PostType.POST,

    val content: String? = null,//for post, event, repost, youtube
    val media: MediaModel? = null,//for post, event
    val location: Location? = null,//for event
    val source: PostResponseDto? = null,//for repost
    val video: String? = null//for youtube
)

enum class PostType {
    POST, EVENT, REPOST, YOUTUBE
}
