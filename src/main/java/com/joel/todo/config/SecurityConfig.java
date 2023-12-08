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
// This annotation indicates that this class provides @Bean methods and may be processed by the Spring container to generate bean definitions and service requests for those beans at runtime
@EnableWebSecurity  // This annotation and the WebSecurityConfigurerAdapter work together to provide web based security
@EnableMethodSecurity  // This annotation is used to enable method level security
public class SecurityConfig {

    // RSAKeyProperties instance which holds the properties of RSA keys
    private final RSAKeyProperties keys;

    // Autowired annotation is used to automatically inject the RSAKeyProperties instance
    @Autowired
    public SecurityConfig(RSAKeyProperties keys) {
        this.keys = keys;
    }

    // Bean definition for PasswordEncoder, responsible for encoding and decoding passwords.
    // BCryptPasswordEncoder is a password encoder that uses the BCrypt strong hashing function
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean definition for AuthenticationManager.
    // AuthenticationManager is responsible for passing requests through a chain of AuthenticationProviders
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService detailsService) {

        // DaoAuthenticationProvider is an AuthenticationProvider that uses a Data Access Object (DAO) to retrieve user information from a relational database
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(detailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        // ProviderManager is a concrete implementation of AuthenticationManager
        return new ProviderManager(daoAuthenticationProvider);

    }

    // Bean definition for SecurityFilterChain.
    // SecurityFilterChain is an interface that defines a chain of Spring Security filters
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disabling CSRF (Cross-Site Request Forgery) protection
                .csrf(AbstractHttpConfigurer::disable)
                // Configuring authorization rules for HTTP requests
                .authorizeHttpRequests(auth -> {
                    // Permitting all requests to certain URLs
                    auth.requestMatchers(
                                    "/users/login",
                                    "/users/login/**",
                                    "/users/register",
                                    "/users/register/**",
                                    "users/checkusernameandemail/**",
                                    "/forgot",
                                    "/forgot/**",
                                    "/reset_password",
                                    "/reset_password/**")
                            .permitAll();
                    // Requiring USER role for all requests to "/users/**" and "/users"
                    auth.requestMatchers("/users/**", "/users").hasAnyRole(RoleType.USER.toString());
                });

        // Configuring OAuth2 resource server using JWT authentication
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());

        // Configuring session management to be stateless
        http.sessionManagement(
                session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
        );

        // Enabling Cross-Origin Resource Sharing (CORS)
        http.cors();

        // Building and returning the SecurityFilterChain
        return http.build();
    }

    // Bean definition for JwtDecoder.
    // JwtDecoder is an interface that defines a strategy for JWT decoding
    @Bean
    JwtDecoder jwtDecoder() {
        // Creating a JwtDecoder using the public key from RSAKeyProperties
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }

    // Bean definition for JwtEncoder.
    // JwtEncoder is an interface that defines a strategy for JWT encoding
    @Bean
    public JwtEncoder jwtEncoder() {

        // Building a JWK using the public and private keys from RSAKeyProperties
        JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();

        // Creating a JWKSource with the JWK
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        // Returning a JwtEncoder using the JWKSource
        return new NimbusJwtEncoder(jwks);
    }

    // Bean definition for JwtAuthenticationConverter.
    // JwtAuthenticationConverter is a converter that converts a JWT into an Authentication
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        // JwtGrantedAuthoritiesConverter is a converter that extracts authorities from a JWT
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        // Creating a JwtAuthenticationConverter and setting the JwtGrantedAuthoritiesConverter
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtConverter;
    }
}