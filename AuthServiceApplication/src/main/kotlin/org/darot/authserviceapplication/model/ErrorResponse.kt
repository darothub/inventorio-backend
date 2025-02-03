package org.darot.authserviceapplication.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    override val status: HttpStatus,
    override val message: String,
    val errors: MutableMap<String, String>? = null,
): AuthResponse(
    status,
    message
)
