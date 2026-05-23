package com.oracle.OSfacil.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private static final String ISSUER = "OSFacil";
    private static final int EXPIRATION_MINUTES = 30;

    @Value("${jwt.secret}")
    private String secret;

    public String gerarToken(UserDetails usuario) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(expiracao())
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException e) {
            throw new RegraDeNegocioException("Erro ao gerar token JWT: " + e.getMessage());
        }
    }

    public String verificarToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT decoded = verifier.verify(token);
            return decoded.getSubject();
        } catch (JWTVerificationException e) {
            throw new RegraDeNegocioException("Token JWT invalido ou expirado");
        }
    }

    private Instant expiracao() {
        return Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES).atZone(ZoneOffset.UTC).toInstant();
    }
}
