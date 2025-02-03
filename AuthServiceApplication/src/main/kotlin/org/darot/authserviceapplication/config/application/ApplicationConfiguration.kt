package org.darot.authserviceapplication.config.application

import org.darot.authserviceapplication.repository.AuthUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
@Configuration
class ApplicationConfiguration(
    private val authUserRepository: AuthUserRepository,
) {
    @Bean
    fun provideUserDetailService() = UserDetailsService { email ->
        authUserRepository.findByEmail(email)
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration) = config.authenticationManager

    @Bean
    fun authenticationProvider(): AuthenticationProvider =
        DaoAuthenticationProvider().apply {
            setUserDetailsService(provideUserDetailService())
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}