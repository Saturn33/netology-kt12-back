package ru.netology.saturn33.homework.hw12

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
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
import ru.netology.saturn33.homework.hw12.dto.ErrorResponseDto
import ru.netology.saturn33.homework.hw12.exception.*
import ru.netology.saturn33.homework.hw12.repository.PostRepository
import ru.netology.saturn33.homework.hw12.repository.PostRepositoryInMemoryWithMutexImpl
import ru.netology.saturn33.homework.hw12.repository.UserRepository
import ru.netology.saturn33.homework.hw12.repository.UserRepositoryInMemoryWithMutexImpl
import ru.netology.saturn33.homework.hw12.route.RoutingV1
import ru.netology.saturn33.homework.hw12.service.*

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

        constant(tag = "fcm-db-url") with (environment.config.propertyOrNull("homework.fcm.db-url")?.getString()
            ?: throw ConfigurationException("FCM DB Url is not specified"))
        constant(tag = "fcm-password") with (environment.config.propertyOrNull("homework.fcm.password")?.getString()
            ?: throw ConfigurationException("FCM Password is not specified"))
        constant(tag = "fcm-salt") with (environment.config.propertyOrNull("homework.fcm.salt")?.getString()
            ?: throw ConfigurationException("FCM Salt is not specified"))
        constant(tag = "fcm-path") with (environment.config.propertyOrNull("homework.fcm.path")?.getString()
            ?: throw ConfigurationException("FCM JSON Path is not specified"))

        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService(instance("jwt-secret"), instance("jwt-expire")) }
        bind<PostRepository>() with eagerSingleton { PostRepositoryInMemoryWithMutexImpl() }
        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutexImpl() }
        bind<PostService>() with eagerSingleton { PostService(instance(), instance(), instance(), instance()) }
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
        bind<FCMService>() with eagerSingleton {
            FCMService(instance(tag = "fcm-db-url"), instance(tag = "fcm-password"), instance(tag = "fcm-salt"), instance(tag = "fcm-path"))
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
