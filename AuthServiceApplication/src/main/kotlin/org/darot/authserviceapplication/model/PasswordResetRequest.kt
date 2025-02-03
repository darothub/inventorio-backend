package org.darot.authserviceapplication.model

data class PasswordResetRequest(
    val email: String,
    val newPassword: String
)