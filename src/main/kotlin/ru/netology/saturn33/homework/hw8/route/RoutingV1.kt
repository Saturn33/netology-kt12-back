package ru.netology.saturn33.homework.hw8.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.ParameterConversionException
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import ru.netology.saturn33.homework.hw8.dto.AuthenticationRequestDto
import ru.netology.saturn33.homework.hw8.dto.PostRequestDto
import ru.netology.saturn33.homework.hw8.dto.UserResponseDto
import ru.netology.saturn33.homework.hw8.model.UserModel
import ru.netology.saturn33.homework.hw8.service.FileService
import ru.netology.saturn33.homework.hw8.service.PostService
import ru.netology.saturn33.homework.hw8.service.UserService

@KtorExperimentalAPI
class RoutingV1(
    private val staticPath: String,
    private val postService: PostService,
    private val fileService: FileService,
    private val userService: UserService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            route("/api/v1") {
                static("/static") {
                    files(staticPath)
                }

                route("") {
                    post("/registration") {
                        TODO("регистрация")
                    }
                    post("/authentication") {
                        val input = call.receive<AuthenticationRequestDto>()
                        println(input)
                        val response = userService.authenticate(input)
                        call.respond(response)
                    }
                }

                authenticate {
                    route("/me") {
                        get {
                            val me = call.authentication.principal<UserModel>()
                            call.respond(UserResponseDto.fromModel(me!!))
                        }
                    }

                    route("/posts") {
                        //main post operations
                        get {
                            val response = postService.getAll()
                            call.respond(response)
                        }
                        get("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
                            val response = postService.getById(id, true)
                            call.respond(response)
                        }
                        post {
                            val input = call.receive<PostRequestDto>()
                            val me = call.authentication.principal<UserModel>()
                            val response = postService.save(me!!, input)
                            call.respond(response)
                        }
                        delete("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
                            val me = call.authentication.principal<UserModel>()
                            postService.delete(me!!, id)
                            call.respond("")
                        }

                        //likes
                        post("/{id}/like") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
                            val response = postService.like(id)
                            call.respond(response)
                        }
                        delete("/{id}/like") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
                            val response = postService.dislike(id)
                            call.respond(response)
                        }

                        //repost
                        post("/{id}/repost") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
                            val input = call.receive<PostRequestDto>()
                            val me = call.authentication.principal<UserModel>()

                            val response = postService.repost(me!!, id, input)
                            call.respond(response)
                        }

                        //share
                        post("/{id}/share") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
                            val response = postService.share(id)
                            call.respond(response)
                        }
                    }

                    //загрузка media
                    route("/media") {
                        post {
                            val multipart = call.receiveMultipart()
                            val response = fileService.save(multipart)
                            call.respond(response)
                        }
                    }
                }
            }
        }
    }
}
