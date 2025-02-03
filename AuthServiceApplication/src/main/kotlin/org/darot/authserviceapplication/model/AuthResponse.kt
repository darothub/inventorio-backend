package org.darot.authserviceapplication.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
open class AuthResponse (open val status: HttpStatus, open val message: String)