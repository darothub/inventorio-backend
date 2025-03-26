package org.darot.authserviceapplication.presentation.controller

import jakarta.validation.Valid
import org.darot.authserviceapplication.presentation.AppConstant
import org.darot.authserviceapplication.core.service.AuthenticationService
import org.darot.authserviceapplication.presentation.dto.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(AppConstant.BASE_URL)
class AuthenticationController(private val authenticationService: AuthenticationService) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<AuthResponse> =
        buildResponseEntity(status = HttpStatus.CREATED, responses = authenticationService.registerUser(signUpRequest))

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> =
        buildResponseEntity(responses = authenticationService.loginUser(loginRequest))

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestHeader("Authorization") token: String): ResponseEntity<AuthResponse> =
        buildResponseEntity(responses = authenticationService.refreshAccessToken(token))

    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: PasswordResetRequest): ResponseEntity<AuthResponse> =
        buildResponseEntity(responses = authenticationService.resetPassword(request))

    private fun buildResponseEntity(status: HttpStatus = HttpStatus.OK, responses: AuthResponse): ResponseEntity<AuthResponse> =
        ResponseEntity.status(status).body(responses)
}