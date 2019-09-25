package ru.netology.saturn33.homework.hw7.dto

import ru.netology.saturn33.homework.hw7.model.Location
import ru.netology.saturn33.homework.hw7.model.PostModel
import ru.netology.saturn33.homework.hw7.model.PostType
import java.util.*

data class PostResponseDto(
    val id: Long,
    val created: Date = Date(),
    val author: String,
    val likes: Int,
    val views: Int,
    val postType: PostType = PostType.POST,
    val content: String? = null,//for post, event, repost, youtube
    val location: Location? = null,//for event
    val source: PostModel? = null,//for repost
    val video: String? = null//for youtube
) {
    companion object {
        fun fromModel(model: PostModel) = PostResponseDto(
            id = model.id,
            created = model.created,
            author = model.author,
            likes = model.likes,
            views = model.views,
            postType = model.postType,
            content = model.content,
            location = model.location,
            source = model.source,
            video = model.video
        )
    }
}
