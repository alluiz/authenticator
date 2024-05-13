package com.example.demo.security;

import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashCrypt {

    public static boolean matches(String plainText, String hash) {

        String hashResult = hash(plainText);

        return hashResult.equals(hash);

    }

    public static String hash(@NonNull String plainText) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));

            // Convert the hash bytes to a hexadecimal string
            return bytesToHex(encodedHash);

        } catch (Exception e) {
            throw new SecurityException("Error while hashing value.", e);
        }
    }

    private static String bytesToHex(byte[] hash) {

        StringBuilder hexString = new StringBuilder(2 * hash.length);

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();

    }
}
