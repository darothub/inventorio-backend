package org.darot.authserviceapplication.presentation.exception

data class BadRequestException(override val message: String): RuntimeException(message)
