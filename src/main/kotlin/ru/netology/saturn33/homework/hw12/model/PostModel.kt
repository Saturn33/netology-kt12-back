package ru.netology.saturn33.homework.hw12.model

import ru.netology.saturn33.homework.hw12.dto.PostResponseDto
import ru.netology.saturn33.homework.hw11.dto.AttachmentModel
import java.util.*

data class PostModel(
    val id: Long,
    val created: Date = Date(),
    val author: Long,
    val source: PostResponseDto? = null,//for repost
    val content: String? = null,//for post, event, repost, youtube
    val likes: Set<Long> = setOf(),
    val reposts: Set<Long> = setOf(),
    val views: Int = 0,
//    val shares: Int = 0,
    val postType: PostType = PostType.POST,
    val attachment: AttachmentModel? = null

//    val media: MediaModel? = null,//for post, event
//    val location: Location? = null,//for event
//    val video: String? = null//for youtube
)

enum class PostType {
    POST, REPOST//, EVENT, YOUTUBE
}
