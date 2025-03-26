package org.darot.authserviceapplication.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.darot.authserviceapplication.presentation.exception.InvalidTokenException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {
    private val secret = "65dbe70cb66174f78717a9d81ad847732616aa425f913c108311683b4a839452"
    private val accessTokenExpirationTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24 //24 hours
    private val refreshTokenExpirationTime = accessTokenExpirationTime * 30 //30 days


    fun generateAccessToken(email: String): String = generateAccessToken(hashMapOf(), email)

    fun generateRefreshToken(
        email: String
    ): String = generateRefreshToken(hashMapOf(), email)

    fun validateToken(token: String): Boolean = extractExpiration(token).after(Date())

    fun getTokenSubject(token:String) = extractClaim(token, Claims::getSubject)

    private fun generateAccessToken(
        extraClaims: Map<String, Any?>,
        email: String
    ): String = buildToken(extraClaims, email, accessTokenExpirationTime)

    private fun generateRefreshToken(
        extraClaims: Map<String, Any?>,
        email: String
    ): String = buildToken(extraClaims, email, refreshTokenExpirationTime)


    private fun buildToken(
        extraClaims: Map<String, Any?>,
        email: String,
        expiration: Long
    ): String =
        Jwts
            .builder()
            .claims(extraClaims)
            .subject(email)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(expiration))
            .signWith(getSigningKey())
            .compact()

    private fun <T> extractClaim(token: String?, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token!!)
        return claimsResolver.invoke(claims)
    }

    private fun extractExpiration(token: String) = extractClaim(token, Claims::getExpiration)

    private fun extractAllClaims(token: String): Claims = try {
        Jwts
            .parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    } catch (e: ExpiredJwtException){
        throw InvalidTokenException("Invalid JWT signature")
    } catch (e: SignatureException) {
        throw IllegalArgumentException(e)
    }

    private fun getSigningKey(): SecretKey {
        val keyBytes: ByteArray = Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}