package ru.netology.saturn33.homework.hw7.route

import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import ru.netology.saturn33.homework.hw7.dto.PostRequestDto
import ru.netology.saturn33.homework.hw7.dto.PostResponseDto
import ru.netology.saturn33.homework.hw7.model.PostModel
import ru.netology.saturn33.homework.hw7.model.PostType
import ru.netology.saturn33.homework.hw7.repository.PostRepository

@KtorExperimentalAPI
fun Routing.v1() {
    route("/api/v1/posts") {
        val repo by kodein().instance<PostRepository>()

        //main post operations
        get {
            val response = repo.getAll().map { PostResponseDto.fromModel(it) }
            call.respond(response)
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val model = repo.getById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(model)
            call.respond(response)
        }
        post {
            val input = call.receive<PostRequestDto>()
            val model = when (input.postType) {
                PostType.POST -> PostModel(
                    id = input.id,
                    author = input.author,
                    postType = input.postType,
                    content = input.content,
                    views = 0
                )
                PostType.EVENT -> if (PostModel.Validator.checkLocation(input.location)) PostModel(
                    id = input.id,
                    author = input.author,
                    postType = input.postType,
                    content = input.content,
                    location = input.location,
                    views = 0
                ) else throw ParameterConversionException("location", "Location")
                PostType.YOUTUBE -> if (PostModel.Validator.checkYoutube(input.video)) PostModel(
                    id = input.id,
                    author = input.author,
                    postType = input.postType,
                    content = input.content,
                    video = input.video,
                    views = 0
                ) else throw ParameterConversionException("video", "YouTube URL")
                PostType.REPOST -> {
                    if (PostModel.Validator.checkSource(repo, input.source)) PostModel(
                        id = input.id,
                        author = input.author,
                        postType = input.postType,
                        content = input.content,
                        source = input.source,
                        views = 0
                    ) else throw ParameterConversionException("source", "PostModel")
                }
            }

            val savedModel = repo.save(model)
            val response = PostResponseDto.fromModel(savedModel)
            call.respond(response)
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            if (repo.removeById(id)) call.respond("") else throw NotFoundException()
        }
        //likes
        post("/{id}/like") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val model = repo.likeById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(model)
            call.respond(response)
        }
        delete("/{id}/like") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val model = repo.dislikeById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(model)
            call.respond(response)
        }
    }
}
