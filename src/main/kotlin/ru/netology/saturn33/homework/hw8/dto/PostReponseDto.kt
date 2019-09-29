package ru.netology.saturn33.homework.hw8.dto

import ru.netology.saturn33.homework.hw8.model.Location
import ru.netology.saturn33.homework.hw8.model.PostModel
import ru.netology.saturn33.homework.hw8.model.PostType
import ru.netology.saturn33.homework.hw8.model.UserModel
import java.util.*
import javax.print.attribute.standard.Media

data class PostResponseDto(
    val id: Long,
    val created: Date = Date(),
    val author: UserResponseDto,
    val likes: Int,
    val views: Int,
    val shares: Int,
    val postType: PostType = PostType.POST,
    val content: String? = null,//for post, event, repost, youtube
    val media: Media? = null,
    val location: Location? = null,//for event
    val source: PostResponseDto? = null,//for repost
    val video: String? = null//for youtube
) {
    companion object {
        fun fromModel(user: UserModel, model: PostModel) = PostResponseDto(
            id = model.id,
            created = model.created,
            author = UserResponseDto.fromModel(user),
            likes = model.likes,
            views = model.views,
            shares = model.shares,
            postType = model.postType,
            content = model.content,
            media = model.media,
            location = model.location,
            source = model.source,
            video = model.video
        )
    }
}
