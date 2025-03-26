package org.darot.authserviceapplication.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.darot.authserviceapplication.core.service.AuthenticationService
import org.darot.authserviceapplication.presentation.dto.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthenticationController::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val email = "test@example.com"
    private val password = "Password1$"
    private val role = "BUYER"

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `registerUser should return 201 CREATED when registration is successful`() {
        // Arrange
        val signUpRequest = SignUpRequest(email, password, role)
        val authResponse = Success("User registered successfully.", null)
        `when`(authenticationService.registerUser(signUpRequest)).thenReturn(authResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("User registered successfully."))
    }

    @Test
    fun `login should return 200 OK when login is successful`() {
        // Arrange
        val loginRequest = LoginRequest(email, password)
        val authResponse = Success("User logged in.", LoginData("accessToken", "refreshToken"))
        `when`(authenticationService.loginUser(loginRequest)).thenReturn(authResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User logged in."))
            .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
            .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"))
    }

    @Test
    fun `refreshToken should return 200 OK when token is valid`() {
        // Arrange
        val token = "validToken"
        val authResponse = Success("Successful.", LoginData("newAccessToken", "newRefreshToken"))
        `when`(authenticationService.refreshAccessToken(token)).thenReturn(authResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/user/refresh")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Successful."))
            .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
            .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken"))
    }

    @Test
    fun `resetPassword should return 200 OK when password reset is successful`() {
        // Arrange
        val request = PasswordResetRequest(email, "newPassword")
        val authResponse = Success("Password reset successful.", null)
        `when`(authenticationService.resetPassword(request)).thenReturn(authResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/user/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Password reset successful."))
    }
}