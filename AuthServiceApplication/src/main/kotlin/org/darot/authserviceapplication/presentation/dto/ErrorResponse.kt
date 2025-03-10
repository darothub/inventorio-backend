package org.darot.authserviceapplication.presentation.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val status: HttpStatus,
    val message: String,
    val errors: MutableMap<String, String>? = null,
)
