package ru.netology.saturn33.homework.hw8.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.saturn33.homework.hw8.model.PostModel

class PostRepositoryInMemoryWithMutexImpl : PostRepository {
    private var nextId = 1L
    private val items = mutableListOf<PostModel>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<PostModel> {
        mutex.withLock {
            items.forEachIndexed { ind, post ->
                items[ind] = post.copy(views = post.views + 1)
            }
            return items.reversed()
        }
    }

    override suspend fun getById(id: Long, incrementViews: Boolean): PostModel? {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    if (incrementViews) items[index] = items[index].copy(views = items[index].views + 1)
                    items[index]
                }
            }
        }
    }

    override suspend fun save(item: PostModel): PostModel {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == item.id }) {
                -1 -> {
                    val copy = item.copy(id = nextId++)
                    items.add(copy)
                    copy
                }
                else -> {
                    //не затирать поля, не зависящие от контента (время добавления, лайки, просмотры, автора)
                    val copy = item.copy(
                        created = items[index].created,
                        likes = items[index].likes,
                        views = items[index].views,
                        author = items[index].author
                    )
                    items[index] = copy
                    copy
                }
            }
        }
    }

    override suspend fun removeById(id: Long): Boolean {
        mutex.withLock {
            return items.removeIf { it.id == id }
        }
    }

    override suspend fun likeById(id: Long): PostModel? {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val item = items[index]
                    val copy = item.copy(likes = item.likes + 1)
                    items[index] = copy
                    copy
                }
            }
        }
    }

    override suspend fun dislikeById(id: Long): PostModel? {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val item = items[index]
                    val copy = item.copy(likes = if (item.likes - 1 < 0) 0 else item.likes - 1)
                    items[index] = copy
                    copy
                }
            }
        }
    }

    override suspend fun shareById(id: Long): PostModel? {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val item = items[index]
                    val copy = item.copy(shares = item.shares + 1)
                    items[index] = copy
                    copy
                }
            }
        }
    }
}