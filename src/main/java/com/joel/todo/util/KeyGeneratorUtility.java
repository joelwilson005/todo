package com.joel.todo.util;
// Import statements for necessary Java security classes

import java.security.KeyPair;
import java.security.KeyPairGenerator;

// Utility class for generating RSA key pairs
public class KeyGeneratorUtility {

    // Method to generate a new RSA key pair
    public static KeyPair generateRsaKey() {

        KeyPair keyPair;

        try {
            // Initialize a KeyPairGenerator for RSA with a key size of 2048 bits
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            // Generate a new RSA key pair
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            // If an exception occurs during key generation, throw an IllegalStateException
            throw new IllegalStateException("Error generating RSA key pair", e);
        }

        // Return the generated RSA key pair
        return keyPair;
    }
}
