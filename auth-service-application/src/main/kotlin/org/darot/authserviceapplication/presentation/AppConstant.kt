package org.darot.authserviceapplication.presentation

object AppConstant {
    const val BASE_URL = "/api/v1/user"
    const val PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
    const val PASSWORD_ADVICE = "Password must contain at least 8 characters, one uppercase, one lowercase, one number, and one special character"
}