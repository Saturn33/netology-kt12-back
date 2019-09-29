package ru.netology.saturn33.homework.hw8.service

import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.util.KtorExperimentalAPI
import ru.netology.saturn33.homework.hw8.dto.PostRequestDto
import ru.netology.saturn33.homework.hw8.dto.PostResponseDto
import ru.netology.saturn33.homework.hw8.exception.ForbiddenException
import ru.netology.saturn33.homework.hw8.model.PostModel
import ru.netology.saturn33.homework.hw8.model.PostType
import ru.netology.saturn33.homework.hw8.model.UserModel
import ru.netology.saturn33.homework.hw8.repository.PostRepository

@KtorExperimentalAPI
class PostService(
    private val repo: PostRepository,
    private val userService: UserService,
    private val validatorService: ValidatorService
) {
    suspend fun getAll(): List<PostResponseDto> {
        return repo.getAll().map { PostResponseDto.fromModel(userService.getModelById(it.author)!!, it) }
    }

    suspend fun getModelById(id: Long): PostModel? {
        return repo.getById(id)
    }

    suspend fun getById(id: Long, incrementView: Boolean = false): PostResponseDto {
        val model = repo.getById(id, incrementView) ?: throw NotFoundException()
        return PostResponseDto.fromModel(userService.getModelById(model.author)!!, model)
    }

    suspend fun save(user: UserModel, input: PostRequestDto): PostResponseDto {
        repo.getById(input.id)?.let {
            if (it.author != user.id) throw ForbiddenException("Wrong author")
        }

        val model = when (input.postType) {
            PostType.POST -> PostModel(
                id = input.id,
                author = user.id,
                postType = input.postType,
                content = input.content,
                media = input.media,
                views = 0
            )
            PostType.EVENT -> if (validatorService.checkLocation(input.location)) PostModel(
                id = input.id,
                author = user.id,
                postType = input.postType,
                content = input.content,
                location = input.location,
                media = input.media,
                views = 0
            ) else throw ParameterConversionException("location", "Location")
            PostType.YOUTUBE -> if (validatorService.checkYoutube(input.video)) PostModel(
                id = input.id,
                author = user.id,
                postType = input.postType,
                content = input.content,
                video = input.video,
                views = 0
            ) else throw ParameterConversionException("video", "YouTube URL")
            else -> throw ParameterConversionException("postType", "PostType")
        }
        return PostResponseDto.fromModel(user, repo.save(model))
    }

    suspend fun delete(user: UserModel, id: Long) {
        val post = repo.getById(id)
        if (post == null) {
            throw NotFoundException()
        } else {
            if (post.author != user.id) throw ForbiddenException("Wrong author")
        }
        repo.removeById(id)
    }

    suspend fun like(id: Long): PostResponseDto {
        val model = repo.likeById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(userService.getModelById(model.author)!!, model)
    }

    suspend fun dislike(id: Long): PostResponseDto {
        val model = repo.dislikeById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(userService.getModelById(model.author)!!, model)
    }

    suspend fun repost(user: UserModel, postId: Long, input: PostRequestDto): PostResponseDto {
        val validResult = validatorService.checkSource(postId, getModelById(postId))
        if (validResult.first) {
            val model = PostModel(
                id = input.id,
                author = user.id,
                postType = PostType.REPOST,
                content = input.content,
                media = input.media,
                source = if (validResult.second != null) PostResponseDto.fromModel(
                    userService.getModelById((validResult.second)!!.author)!!,
                    validResult.second as PostModel
                ) else null,
                views = 0
            )
            val savedModel = repo.save(model)
            return PostResponseDto.fromModel(user, savedModel)
        } else throw ParameterConversionException("source", "PostModel")
    }

    suspend fun share(id: Long): PostResponseDto {
        val model = repo.shareById(id) ?: throw NotFoundException("Source post not found")
        return PostResponseDto.fromModel(userService.getModelById(model.author)!!, model)
    }
}
