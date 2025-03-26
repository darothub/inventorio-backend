package org.darot.authserviceapplication.presentation.dto

import com.fasterxml.jackson.annotation.JsonInclude

sealed class AuthResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Success<T>(
    val message: String,
    val data: T? = null
): AuthResponse()