package org.darot.authserviceapplication.presentation.dto

data class PasswordResetRequest(
    val email: String,
    val newPassword: String
)