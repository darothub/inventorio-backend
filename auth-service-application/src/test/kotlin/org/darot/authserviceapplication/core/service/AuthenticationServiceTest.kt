package org.darot.authserviceapplication.core.service

import org.darot.authserviceapplication.core.model.AuthUser
import org.darot.authserviceapplication.core.model.UserRole
import org.darot.authserviceapplication.infrastructure.db.AuthUserRepository
import org.darot.authserviceapplication.infrastructure.security.JwtUtil
import org.darot.authserviceapplication.presentation.dto.LoginRequest
import org.darot.authserviceapplication.presentation.dto.PasswordResetRequest
import org.darot.authserviceapplication.presentation.dto.SignUpRequest
import org.darot.authserviceapplication.presentation.exception.BadRequestException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder

class AuthenticationServiceTest {
    @Mock
    private lateinit var authUserRepository: AuthUserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private lateinit var jwtUtil: JwtUtil

    @InjectMocks
    private lateinit var authenticationService: AuthenticationService
    private val email = "test@example.com"
    private val password = "password"
    private val role = "BUYER"
    private val encodedPassword = "encodedPassword"
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `registerUser should throw IllegalArgumentException when email already exists`() {

        // Arrange
        val signUpRequest = SignUpRequest(email, "password", role)
        `when`(authUserRepository.findByEmail(email)).thenReturn(AuthUser(1, email, password, UserRole.valueOf(role)))

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            authenticationService.registerUser(signUpRequest)
        }
    }

    @Test
    fun `registerUser should return success response when user is registered`() {
        // Arrange
        val signUpRequest = SignUpRequest(email, password, role)
        `when`(authUserRepository.findByEmail(email)).thenReturn(null)
        `when`(passwordEncoder.encode(password)).thenReturn(encodedPassword)

        // Act
        val result = authenticationService.registerUser(signUpRequest)

        // Assert
        assertEquals("User registered successfully.", result.message)
        verify(authUserRepository, times(1)).save(any<AuthUser>())
    }
    @Test
    fun `loginUser should return success response with tokens when authentication is successful`() {
        // Arrange
        val loginRequest = LoginRequest(email, password)
        val authUser = AuthUser(email = email, password = encodedPassword, role = UserRole.BUYER)
        val authentication = UsernamePasswordAuthenticationToken(authUser, null)
        `when`(authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>())).thenReturn(authentication)
        `when`(jwtUtil.generateAccessToken(email)).thenReturn("accessToken")
        `when`(jwtUtil.generateRefreshToken(email)).thenReturn("refreshToken")

        // Act
        val result = authenticationService.loginUser(loginRequest)

        // Assert
        assertEquals("User logged in.", result.message)
        assertNotNull(result.data)
    }

    @Test
    fun `loginUser should throw BadRequestException when authentication fails`() {
        // Arrange
        val loginRequest = LoginRequest("test@example.com", "password")
        `when`(authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>())).thenThrow(
            BadRequestException("Invalid username or password.")
        )

        // Act & Assert
        assertThrows(BadRequestException::class.java) {
            authenticationService.loginUser(loginRequest)
        }
    }

    @Test
    fun `refreshAccessToken should return success response with new tokens when token is valid`() {
        // Arrange
        val token = "validToken"
        `when`(jwtUtil.validateToken(token)).thenReturn(true)
        `when`(jwtUtil.getTokenSubject(token)).thenReturn(email)
        `when`(jwtUtil.generateAccessToken(email)).thenReturn("newAccessToken")
        `when`(jwtUtil.generateRefreshToken(email)).thenReturn("newRefreshToken")

        // Act
        val result = authenticationService.refreshAccessToken(token)

        // Assert
        assertEquals("Successful.", result.message)
        assertNotNull(result.data)
    }

    @Test
    fun `refreshAccessToken should throw BadRequestException when token is invalid`() {
        // Arrange
        val token = "invalidToken"
        `when`(jwtUtil.validateToken(token)).thenReturn(false)

        // Act & Assert
        assertThrows(BadRequestException::class.java) {
            authenticationService.refreshAccessToken(token)
        }
    }

    @Test
    fun `resetPassword should return success response when password is reset`() {
        // Arrange
        val request = PasswordResetRequest(email, "newPassword")
        val user = AuthUser(email = email, password = "oldPassword", role = UserRole.BUYER)
        `when`(authUserRepository.findByEmail(email)).thenReturn(user)
        `when`(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword")

        // Act
        val result = authenticationService.resetPassword(request)

        // Assert
        assertEquals("Password reset successful.", result.message)
        verify(authUserRepository, times(1)).save(user.copy(password = "encodedNewPassword"))
    }

    @Test
    fun `resetPassword should throw BadRequestException when user is not found`() {
        // Arrange
        val request = PasswordResetRequest(email, "newPassword")
        `when`(authUserRepository.findByEmail(email)).thenReturn(null)

        // Act & Assert
        assertThrows(BadRequestException::class.java) {
            authenticationService.resetPassword(request)
        }
    }
}