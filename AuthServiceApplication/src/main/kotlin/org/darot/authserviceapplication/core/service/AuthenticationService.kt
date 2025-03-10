package org.darot.authserviceapplication.core.service

import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.darot.authserviceapplication.core.model.AuthUser
import org.darot.authserviceapplication.core.model.UserRole
import org.darot.authserviceapplication.infrastructure.db.AuthUserRepository
import org.darot.authserviceapplication.infrastructure.security.JwtUtil
import org.darot.authserviceapplication.presentation.dto.*
import org.darot.authserviceapplication.presentation.exception.BadRequestException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
) {

    fun registerUser(signUpRequest: SignUpRequest): Success<Nothing> {

        if (authUserRepository.findByEmail(signUpRequest.email) != null) {
            throw IllegalArgumentException("Email already exists")
        }
        val newUser = AuthUser(
            email = signUpRequest.email,
            password = passwordEncoder.encode(signUpRequest.password),
            role = UserRole.valueOf(signUpRequest.role.uppercase(Locale.getDefault()))
        )
        authUserRepository.save(newUser)
        return Success( "User registered successfully.", data = null)
    }

    @RateLimiter(name = "myRateLimiter")
    fun loginUser(loginRequest: LoginRequest): Success<LoginData> {
        return try {
            // Authenticate user
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )
            val authUser = authentication.principal as? AuthUser
                ?: throw BadRequestException("Invalid username or password.")

            // Generate tokens
            val accessToken = jwtUtil.generateAccessToken(authUser.username)
            val refreshToken = jwtUtil.generateRefreshToken(authUser.username)

            // Prepare response
            Success("User logged in.", LoginData(accessToken, refreshToken))
        } catch (e: Exception){
            throw BadRequestException("Invalid username or password.")
        }
    }

    fun refreshAccessToken(token: String): Success<LoginData> {
        val isValidToken = jwtUtil.validateToken(token)
        if (!isValidToken) {
            throw BadRequestException("User should login again")
        }
        val subject = jwtUtil.getTokenSubject(token)
        val accessToken = jwtUtil.generateAccessToken(subject)
        val refreshToken = jwtUtil.generateRefreshToken(subject)
        val loginData = LoginData(accessToken = accessToken, refreshToken = refreshToken)
        return Success("Successful.", data = loginData)
    }

    fun resetPassword(request: PasswordResetRequest): Success<Nothing> {
        val user = authUserRepository.findByEmail(request.email)
            ?: throw BadRequestException("User not found")

        val hashedPassword = passwordEncoder.encode(request.newPassword)
        authUserRepository.save(user.copy(password = hashedPassword))
        return Success("Password reset successful.", data = null)
    }

}