package com.joel.todo.config;

import com.joel.todo.model.RoleType;
import com.joel.todo.util.RSAKeyProperties;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Inject RSAKeyProperties instance via constructor.
    private final RSAKeyProperties keys;

    @Autowired
    public SecurityConfig(RSAKeyProperties keys) {
        this.keys = keys;
    }

    // Bean definition for PasswordEncoder, responsible for encoding and decoding passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean definition for AuthenticationManager.
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService detailsService) {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(detailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(daoAuthenticationProvider);

    }

    // Bean definition for SecurityFilterChain.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                                    "/users/login",
                                    "/users/login/**",
                                    "/users/register",
                                    "/users/register/**",
                                    "/forgot",
                                    "/forgot/**",
                                    "/reset_password",
                                    "/reset_password/**")
                            .permitAll();
                    auth.requestMatchers("/users/**", "/users").hasAnyRole(RoleType.USER.toString());
                });

        // Configure OAuth2 resource server using JWT authentication.
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());

        // Configure session management to be stateless.
        http.sessionManagement(
                session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
        );

        // Build and return the SecurityFilterChain.
        return http.build();
    }

    // Bean definition for JwtDecoder.
    @Bean
    JwtDecoder jwtDecoder() {
        // Create a JwtDecoder using the public key from RSAKeyProperties.
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }

    // Bean definition for JwtEncoder.
    @Bean
    public JwtEncoder jwtEncoder() {

        // Build a JWK using the public and private keys from RSAKeyProperties.
        JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();

        // Create a JWKSource with the JWK.
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        // Return a JwtEncoder using the JWKSource.
        return new NimbusJwtEncoder(jwks);
    }

    // Bean definition for JwtAuthenticationConverter.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtConverter;

    }
}
