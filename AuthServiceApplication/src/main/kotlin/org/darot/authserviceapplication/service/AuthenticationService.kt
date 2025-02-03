package org.darot.authserviceapplication.service

import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.darot.authserviceapplication.config.security.JwtUtil
import org.darot.authserviceapplication.entity.AuthUser
import org.darot.authserviceapplication.entity.UserRole
import org.darot.authserviceapplication.model.*
import org.darot.authserviceapplication.repository.AuthUserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
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

    fun registerUser(signUpRequest: SignUpRequest): AuthResponse {

        if (authUserRepository.findByEmail(signUpRequest.email) != null) {
            throw IllegalArgumentException("Email already exists")
        }
        val newUser = AuthUser(
            email = signUpRequest.email,
            password = passwordEncoder.encode(signUpRequest.password),
            role = UserRole.valueOf(signUpRequest.role.uppercase(Locale.getDefault()))
        )
        authUserRepository.save(newUser)
        return AuthResponse(status = HttpStatus.CREATED, "User registered successfully.")
    }
    @RateLimiter(name = "myRateLimiter", fallbackMethod = "rateLimiterFallback")
    fun loginUser(loginRequest: LoginRequest): AuthResponse {
        return try {
            // Authenticate user
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )
            val authUser = authentication.principal as? AuthUser
                ?: return AuthResponse(HttpStatus.BAD_REQUEST, "Invalid username or password.")

            // Generate tokens
            val accessToken = jwtUtil.generateAccessToken(authUser.username)
            val refreshToken = jwtUtil.generateRefreshToken(authUser.username)

            // Prepare response
            val loginData = LoginData(accessToken, refreshToken)
            LoginResponse(HttpStatus.OK, "User logged in.", loginData)
        } catch (e: BadCredentialsException) {
            // Handle BadCredentialsException errors
            AuthResponse(HttpStatus.BAD_REQUEST, "Invalid username or password.")
        } catch (e: InternalAuthenticationServiceException){
            // Handle InternalAuthenticationServiceException errors
            AuthResponse(HttpStatus.UNAUTHORIZED, "Unauthorized user.")
        } catch (e: Exception){
            AuthResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.message.toString())
        }
    }

    fun refreshAccessToken(refreshTokenRequest: RefreshTokenRequest): AuthResponse {
        val isValidToken = jwtUtil.validateToken(refreshTokenRequest.refreshToken)
        if (!isValidToken) {
           return ErrorResponse(status = HttpStatus.BAD_REQUEST, message = "User should login again")
        }
        val subject = jwtUtil.getTokenSubject(refreshTokenRequest.refreshToken)
        val accessToken = jwtUtil.generateAccessToken(subject)
        val refreshToken = jwtUtil.generateRefreshToken(subject)
        val loginData = LoginData(accessToken = accessToken, refreshToken = refreshToken)
        return LoginResponse(status = HttpStatus.OK, "Successful.", data = loginData)
    }

    fun resetPassword(request: PasswordResetRequest): AuthResponse {
        val user = authUserRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("User not found")

        val hashedPassword = passwordEncoder.encode(request.newPassword)
        authUserRepository.save(user.copy(password = hashedPassword))
        return AuthResponse(HttpStatus.OK, "Password reset successful.")
    }
    fun rateLimiterFallback(loginRequest: LoginRequest, ex: Exception): AuthResponse {
        return AuthResponse(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, "Exceeded limit, please try again later.")
    }
}