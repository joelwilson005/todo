package com.joel.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

// @Service annotation is used to indicate that this class is a Spring service component
@Service
public class JWTTokenService {

    // JwtEncoder is used to encode JWTs
    private final JwtEncoder jwtEncoder;

    // @Autowired annotation is used for automatic dependency injection
    @Autowired
    public JWTTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
    }

    // Method to generate a JWT
    public String generateJwt(Authentication auth, Long userId) {

        // Collecting the authorities of the authenticated user and joining them into a string
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(""));

        // Building the JWT claims set
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // setting the issuer of the JWT
                .issuedAt(Instant.now()) // setting the issued at time of the JWT
                .subject(auth.getName()) // setting the subject of the JWT
                .claim("roles", scope) // adding a custom claim for roles
                .claim("userId", userId) // adding a custom claim for user ID
                .build();

        // Encoding the JWT and returning the token value
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}