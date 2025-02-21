package org.darot.authserviceapplication.presentation.dto

import org.springframework.http.HttpStatus

data class LoginResponse(
    override val status: HttpStatus,
    override val message: String,
    val data: LoginData
): AuthResponse(
    status,
    message
)

data class LoginData(
    val accessToken: String,
    val refreshToken: String
)