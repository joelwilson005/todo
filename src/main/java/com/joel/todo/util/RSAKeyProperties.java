package com.joel.todo.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// Component annotation marks this class as a Spring bean to be automatically managed by the Spring container
@Component
// Lombok annotations for automatic generation of getter and setter methods
@Getter
@Setter
// Utility class for managing RSA public and private keys
public class RSAKeyProperties {

    // Fields to store RSA public and private keys
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    // Constructor to initialize the RSAKeyProperties with a new RSA key pair
    public RSAKeyProperties() {
        // Generate a new RSA key pair using the KeyGeneratorUtility
        KeyPair keyPair = KeyGeneratorUtility.generateRsaKey();

        // Set the public and private keys from the generated key pair
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }
}
