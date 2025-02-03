package org.darot.authserviceapplication.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.darot.authserviceapplication.config.application.AppConstant
import org.darot.authserviceapplication.entity.UserRole
import org.darot.authserviceapplication.model.annotation.ValidUserRole

data class SignUpRequest (
    @field:Email(message = "Invalid email format")
    val email: String,
    @field:Pattern(
        regexp = AppConstant.PASSWORD_PATTERN,
        message = AppConstant.PASSWORD_ADVICE
    )
    val password: String,
    @field:ValidUserRole(enumClass = UserRole::class)
    val role: String
)