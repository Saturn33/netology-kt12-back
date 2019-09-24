package ru.netology.saturn33.homework.hw7.model

import ru.netology.saturn33.homework.hw7.repository.PostRepository
import java.util.*

data class PostModel(
    val id: Long,
    val created: Date = Date(),
    val author: String,
    val likes: Int = 0,
    val views: Int = 0,
    val postType: PostType = PostType.POST,

    val content: String? = null,//for post, event, repost, youtube
    val location: Location? = null,//for event
    val source: Long? = null,//for repost
    val video: String? = null//for youtube, val views: kotlin.Int){}, var views: kotlin.Int){}
) {
    object Validator {
        fun checkYoutube(link: String?): Boolean {
            if (link == null) return false
            return Regex("^(?:https?://)?(?:www\\.)?(?:youtube\\.com/)([a-zA-Z_\\d-]+)$").find(link) != null
        }

        fun checkLocation(loc: Location?): Boolean = loc is Location

        suspend fun checkSource(repo: PostRepository, srcId: Long?): Boolean {
            if (srcId == null) return false
            val model = repo.getById(srcId)
            return model != null
            //TODO добавить проверку на цикличность репостов (если давать править исходный пост, в нём можно поставить ссылку на его репост, так нельзя)
        }
    }
}

enum class PostType {
    POST, EVENT, REPOST, YOUTUBE
}
