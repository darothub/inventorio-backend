package org.darot.authserviceapplication.presentation.dto

data class LoginData(
    val accessToken: String,
    val refreshToken: String
)