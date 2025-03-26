package org.darot.authserviceapplication.presentation.exception

data class InvalidTokenException(override val message: String): RuntimeException(message)