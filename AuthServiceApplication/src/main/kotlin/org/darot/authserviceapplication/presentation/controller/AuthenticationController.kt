package org.darot.authserviceapplication.presentation.controller

import jakarta.validation.Valid
import org.darot.authserviceapplication.presentation.AppConstant
import org.darot.authserviceapplication.core.service.AuthenticationService
import org.darot.authserviceapplication.presentation.dto.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(AppConstant.BASE_URL)
class AuthenticationController(private val authenticationService: AuthenticationService) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<AuthResponse> =
        buildResponseEntity(authenticationService.registerUser(signUpRequest))
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> =
        buildResponseEntity(authenticationService.loginUser(loginRequest))
    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<AuthResponse> =
        buildResponseEntity(authenticationService.refreshAccessToken(refreshTokenRequest))
    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: PasswordResetRequest): ResponseEntity<AuthResponse> =
        buildResponseEntity(authenticationService.resetPassword(request))
    private fun buildResponseEntity(authResponse: AuthResponse): ResponseEntity<AuthResponse> =
        ResponseEntity.status(authResponse.status).body(authResponse)
}