package ru.netology.saturn33.homework.hw10

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.cio.EngineMain
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.with
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.saturn33.homework.hw10.dto.ErrorResponseDto
import ru.netology.saturn33.homework.hw10.exception.ConfigurationException
import ru.netology.saturn33.homework.hw10.exception.ForbiddenException
import ru.netology.saturn33.homework.hw10.exception.InvalidPasswordException
import ru.netology.saturn33.homework.hw10.exception.BadRequestException
import ru.netology.saturn33.homework.hw10.exception.NotFoundException
import ru.netology.saturn33.homework.hw10.exception.ParameterConversionException
import ru.netology.saturn33.homework.hw10.repository.PostRepository
import ru.netology.saturn33.homework.hw10.repository.PostRepositoryInMemoryWithMutexImpl
import ru.netology.saturn33.homework.hw10.repository.UserRepository
import ru.netology.saturn33.homework.hw10.repository.UserRepositoryInMemoryWithMutexImpl
import ru.netology.saturn33.homework.hw10.route.RoutingV1
import ru.netology.saturn33.homework.hw10.service.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@KtorExperimentalAPI
fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }


    install(StatusPages) {
        exception<NotImplementedError> {
            call.respond(HttpStatusCode.NotImplemented, ErrorResponseDto(it.message.toString()))
        }
        exception<NotFoundException> {
            call.respond(HttpStatusCode.NotFound, ErrorResponseDto(it.message.toString()))
        }
        exception<BadRequestException> {
            call.respond(HttpStatusCode.BadRequest, ErrorResponseDto(it.message.toString()))
        }
        exception<ParameterConversionException> {
            call.respond(HttpStatusCode.BadRequest, ErrorResponseDto(it.message.toString()))
        }
        exception<InvalidPasswordException> {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponseDto(it.message.toString()))
        }
        exception<ForbiddenException> {
            call.respond(HttpStatusCode.Forbidden, ErrorResponseDto(it.message.toString()))
        }
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponseDto(it.message.toString()))
        }

    }

    install(KodeinFeature) {
        constant(tag = "upload-dir") with (environment.config.propertyOrNull("homework.upload.dir")?.getString()
            ?: throw ConfigurationException("Upload dir is not specified"))
        constant(tag = "jwt-secret") with (environment.config.propertyOrNull("homework.jwt.secret")?.getString()
            ?: throw ConfigurationException("JWT secret is not specified"))
        constant(tag = "jwt-expire") with (environment.config.propertyOrNull("homework.jwt.expire")?.getString()?.toLong()
            ?: 0L)

        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService(instance("jwt-secret"), instance("jwt-expire")) }
        bind<PostRepository>() with eagerSingleton { PostRepositoryInMemoryWithMutexImpl() }
        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutexImpl() }
        bind<PostService>() with eagerSingleton { PostService(instance(), instance(), instance()) }
        bind<ValidatorService>() with eagerSingleton { ValidatorService() }
        bind<FileService>() with eagerSingleton { FileService(instance("upload-dir")) }
        bind<UserService>() with eagerSingleton {
            UserService(instance(), instance(), instance()).apply {
                runBlocking {
                    this@apply.save("vasya", "password")
                    this@apply.save("petya", "password")
                }
            }
        }
        bind<RoutingV1>() with eagerSingleton { RoutingV1(instance("upload-dir"), instance(), instance(), instance()) }
    }

    install(Authentication) {
        jwt {
            val jwtService by kodein().instance<JWTTokenService>()
            verifier(jwtService.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asLong()
                userService.getModelById(id)
            }
        }
    }
    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }
}
