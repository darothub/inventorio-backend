package org.darot.authserviceapplication.model

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class RefreshTokenRequest(
    @field:NotNull
    @field:NotBlank(message = "Invalid token")
    val refreshToken: String
)