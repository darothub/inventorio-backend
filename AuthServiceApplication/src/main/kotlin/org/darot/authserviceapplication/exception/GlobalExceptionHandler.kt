package org.darot.authserviceapplication.exception

import jakarta.servlet.http.HttpServletRequest
import lombok.extern.slf4j.Slf4j
import org.darot.authserviceapplication.model.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAllOtherException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = ex.message.toString()
        )

        return ResponseEntity(errorResponse, errorResponse.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        val errors = mutableMapOf<String, String>()

        ex.bindingResult.fieldErrors.forEach { error ->
            errors[error.field] = error.defaultMessage ?: "Invalid value"
        }
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Invalid input parameter",
            errors = errors
        )

        return ResponseEntity(errorResponse, errorResponse.status)
    }
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Invalid request parameter",
        )
        return ResponseEntity(errorResponse, errorResponse.status)
    }

}