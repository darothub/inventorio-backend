package org.darot.authserviceapplication.repository

import org.darot.authserviceapplication.entity.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthUserRepository: JpaRepository<AuthUser, Long> {
    fun findByEmail(email: String): AuthUser?
    fun existsByEmail(email: String): Boolean
}