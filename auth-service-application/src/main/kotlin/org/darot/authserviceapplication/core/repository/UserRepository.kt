package org.darot.authserviceapplication.core.repository

import org.darot.authserviceapplication.core.model.AuthUser

interface UserRepository {
    fun findByEmail(email: String): AuthUser?
    fun save(authUser: AuthUser): AuthUser
}