package com.netsentinel.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    /**
     * Deriva la clave HMAC-SHA256 directamente desde el string del secreto.
     * Simple y consistente: misma clave para firmar y validar.
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("[JWT] Token malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("[JWT] Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("[JWT] Token no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("[JWT] Claims vacíos: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[JWT] Error inesperado al validar token: {}", e.getMessage());
        }
        return false;
    }
}
