package ru.netology.saturn33.homework.hw10.dto

import ru.netology.saturn33.homework.hw10.model.PostType
import ru.netology.saturn33.homework.hw11.dto.AttachmentModel

data class PostRequestDto(
    val id: Long,
    val postType: PostType = PostType.POST,
    val content: String? = null,//for post, event, repost, youtube
    val attachment: AttachmentModel? = null
//    val media: MediaModel? = null,
//    val location: Location? = null,//for event
//    val video: String? = null//for youtube
)
