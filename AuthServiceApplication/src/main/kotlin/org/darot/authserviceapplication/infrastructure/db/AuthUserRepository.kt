package org.darot.authserviceapplication.infrastructure.db

import org.darot.authserviceapplication.core.model.AuthUser
import org.darot.authserviceapplication.core.repository.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthUserRepository: UserRepository, JpaRepository<AuthUser, Long> {
    override fun findByEmail(email: String): AuthUser?
}