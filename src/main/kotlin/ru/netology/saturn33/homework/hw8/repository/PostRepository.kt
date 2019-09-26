package ru.netology.saturn33.homework.hw8.repository

import ru.netology.saturn33.homework.hw8.model.PostModel

interface PostRepository {
    suspend fun getAll(): List<PostModel>
    suspend fun getById(id: Long, incrementViews: Boolean = false): PostModel?
    suspend fun save(item: PostModel): PostModel
    suspend fun removeById(id: Long): Boolean
    suspend fun likeById(id: Long): PostModel?
    suspend fun dislikeById(id: Long): PostModel?
    suspend fun shareById(id: Long): PostModel?
}
