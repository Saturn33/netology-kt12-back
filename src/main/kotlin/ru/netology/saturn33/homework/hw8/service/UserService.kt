package ru.netology.saturn33.homework.hw8.service

import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.saturn33.homework.hw8.dto.*
import ru.netology.saturn33.homework.hw8.exception.InvalidPasswordException
import ru.netology.saturn33.homework.hw8.exception.PasswordChangeException
import ru.netology.saturn33.homework.hw8.model.UserModel
import ru.netology.saturn33.homework.hw8.repository.UserRepository

@KtorExperimentalAPI
class UserService(
    private val repo: UserRepository,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    private val mutex = Mutex()

    suspend fun getModelById(id: Long): UserModel? {
        return repo.getById(id)
    }

    suspend fun getById(id: Long): UserResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return UserResponseDto.fromModel(model)
    }

    suspend fun changePassword(id: Long, input: PasswordChangeRequestDto) {
        mutex.withLock {
            val model = repo.getById(id) ?: throw NotFoundException()
            if (!passwordEncoder.matches(input.old, model.password)) {
                throw PasswordChangeException("Wrong password!")
            }
            val copy = model.copy(password = passwordEncoder.encode(input.new))
            repo.save(copy)
        }
    }

    suspend fun register(input: RegistrationRequestDto): AuthenticationResponseDto {
        mutex.withLock {
            if (repo.getByUsername(input.username) != null) throw BadRequestException("Пользователь с таким логином уже зарегистрирован")
            val model = repo.save(UserModel(username = input.username, password = passwordEncoder.encode(input.password)))
            val token = tokenService.generate(model.id)
            return AuthenticationResponseDto(token)
        }
    }

    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val model = repo.getByUsername(input.username) ?: throw NotFoundException("User not found")
        if (!passwordEncoder.matches(input.password, model.password)) {
            throw InvalidPasswordException("Wrong password!")
        }

        val token = tokenService.generate(model.id)
        return AuthenticationResponseDto(token)
    }

    suspend fun save(username: String, password: String) {
        repo.save(UserModel(username = username, password = passwordEncoder.encode(password)))
        return
    }
}
