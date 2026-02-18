package com.syslogmanager.application.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-minutes}")
    private Long expirationMinutes;

    public String generateToken(String username, String role) {
        Algorithm alg = Algorithm.HMAC256(secret);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMinutes * 60 * 1000);
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(alg);
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        Algorithm alg = Algorithm.HMAC256(secret);
        return JWT.require(alg).build().verify(token);
    }

}
