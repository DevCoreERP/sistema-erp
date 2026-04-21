package com.devcoreerp.backend_erp.auth.application.services;

// Java
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

// Spring
import org.springframework.stereotype.Service;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.domain.services.TokenService;

import org.springframework.beans.factory.annotation.Value;

// Spring Security
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

// JWT (Spring Security OAuth2)
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;   //esta en dudad si da?
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

// Logging
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class TokenServiceImpl implements TokenService {
    private final static Logger logger = LogManager.getLogger(TokenServiceImpl.class);
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private int jwtExpiration;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public TokenServiceImpl(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        Usuario currentUser = (Usuario) authentication.getPrincipal();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(currentUser.getEmail())
            .issuedAt(now)
            .expiresAt(now.plus(jwtExpiration, ChronoUnit.MINUTES))
            .build();

        var jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }

    @Override
    public String getUserFromToken(String token) {
        Jwt jwtToken = jwtDecoder.decode(token);
        return jwtToken.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception exception) {
            logger.error("[USER] : Error while trying to validate token", exception);
            throw new BadJwtException("Error while trying to validate token");
        }
    }
}
