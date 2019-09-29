package ru.netology.saturn33.homework.hw8.repository

import ru.netology.saturn33.homework.hw8.model.PostModel
import ru.netology.saturn33.homework.hw8.model.UserModel

interface PostRepository {
    suspend fun getAll(): List<PostModel>
    suspend fun getById(id: Long, incrementViews: Boolean = false): PostModel?
    suspend fun save(item: PostModel): PostModel
    suspend fun removeById(id: Long): Boolean
    suspend fun likeById(user: UserModel, id: Long): PostModel?
    suspend fun dislikeById(user: UserModel, id: Long): PostModel?
    suspend fun shareById(id: Long): PostModel?
}
