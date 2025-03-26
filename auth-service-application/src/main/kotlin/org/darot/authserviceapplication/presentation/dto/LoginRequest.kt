package org.darot.authserviceapplication.presentation.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.darot.authserviceapplication.presentation.AppConstant

data class LoginRequest (
    @field:Email(message = "Invalid email format")
    val email: String,
    @field:Pattern(
        regexp = AppConstant.PASSWORD_PATTERN,
        message = AppConstant.PASSWORD_ADVICE
    )
    val password: String
)