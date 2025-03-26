package org.darot.authserviceapplication.infrastructure.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider
) {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .securityMatcher("/api/v1/user/**")
            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
            .headers { headers -> headers.frameOptions { frame -> frame.sameOrigin() } }
            .cors{ it.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1/user/**").permitAll()
                auth.requestMatchers(PathRequest.toH2Console()).permitAll()
                auth.anyRequest().authenticated()
            }
            .sessionManagement { sess: SessionManagementConfigurer<HttpSecurity?> ->
                sess.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }

        httpSecurity.authenticationProvider(authenticationProvider)
        return httpSecurity.httpBasic{it.disable()}
            .build()
    }
}