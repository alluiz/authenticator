package com.example.authenticator.security;

import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashCrypt {

    public static boolean matches(String plainText, String hash) {

        String hashResult = hash(plainText);

        return hashResult.equals(hash);

    }

    public static String hash(String plainText) {

        Assert.notNull(plainText, "The plaintText must be not null.");
        Assert.hasLength(plainText.trim(), "The plaintText must be not empty.");

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = md.digest(plainText.getBytes(StandardCharsets.UTF_8));

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
