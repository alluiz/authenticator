package com.example.authenticator.security;

import com.example.authenticator.exceptions.RSAServerException;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSACrypt {

    public static final String ALGORITHM = "RSA";
    public static final int KEYSIZE = 2048;

    public static final String ERROR_WHILE_GENERATING_RSA_KEY_PAIR = "Error while generating RSA Key Pair";
    public static final String ERROR_WHILE_LOADING_PRIVATE_KEY = "Error while loading private key from PEM file.";
    public static final String ERROR_WHILE_LOADING_PUBLIC_KEY = "Error while loading public key from PEM file.";
    public static final String ERROR_WHILE_LOADING_PUBLIC_KEY_FROM_PRIVATE_KEY = "Error while loading public key from private key.";

    public static final String ERROR_KEY_ALGORITHM_IS_DIFFERENT = "The key algorithm is different.";
    public static final String ERROR_KEY_INVALID = "The key is invalid.";
    public static final String ERROR_KEY_DESTROYED = "The private key is destroyed.";

    public static PrivateKey readRSAPrivateKeyFromPEM(String privateKeyPEM) {

        Assert.notNull(privateKeyPEM, "The privateKeyPEM must be not null.");
        Assert.hasLength(privateKeyPEM.trim(), "The privateKeyPEM must be not empty.");

        try {

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            var cleanPrivateKeyPEM = privateKeyPEM
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");

            KeyFactory keyFactory = getKeyFactory();
            PKCS8EncodedKeySpec keySpec = getPkcs8EncodedKeySpec(cleanPrivateKeyPEM);

            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new RSAServerException(ERROR_WHILE_LOADING_PRIVATE_KEY, e);
        }

    }

    public static PublicKey readRSAPublicKeyFromPEM(String publicKeyPEM) {

        Assert.notNull(publicKeyPEM, "The publicKeyPEM must be not null.");
        Assert.hasLength(publicKeyPEM.trim(), "The publicKeyPEM must be not empty.");

        try {

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            var cleanPublicKeyPEM = publicKeyPEM
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "");

            KeyFactory keyFactory = getKeyFactory();
            X509EncodedKeySpec keySpec = getX509EncodedKeySpec(cleanPublicKeyPEM);

            return keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
            throw new RSAServerException(ERROR_WHILE_LOADING_PUBLIC_KEY, e);
        }

    }

    public static PublicKey readRSAPublicKeyFromPrivateKey(PrivateKey privateKey) {

        Assert.notNull(privateKey, "The privateKey must be not null.");
        Assert.isTrue(!privateKey.isDestroyed(), "The privateKey must be not destroyed.");

        try {

            // Generate the public key from the private key
            KeyFactory keyFactory = getKeyFactory();
            RSAPrivateCrtKey privk = (RSAPrivateCrtKey)privateKey;

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());

            return keyFactory.generatePublic(publicKeySpec);

        } catch (Exception e) {
            throw new RSAServerException(ERROR_WHILE_LOADING_PUBLIC_KEY_FROM_PRIVATE_KEY, e);
        }

    }

    public static KeyPair generateKeyPair()  {

        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEYSIZE); // Key size (bits)

            return keyPairGenerator.generateKeyPair();

        } catch (Exception e) {
            throw new SecurityException(ERROR_WHILE_GENERATING_RSA_KEY_PAIR, e);
        }
    }

    public static String encrypt(String plainText, Key key) {

        Assert.notNull(plainText, "The plaintText must be not null.");
        Assert.hasLength(plainText.trim(), "The plainText must be not empty.");

        Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);

        try {

            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = encode(cipher.doFinal(plainBytes));

            return new String(encryptedBytes);

        } catch (Exception e) {
            throw new SecurityException("Error while crypting with RSA algorithm.", e);
        }

    }

    public static String decrypt(String encryptedText, Key key) {

        Assert.notNull(encryptedText, "The encryptedText must be not null.");
        Assert.hasLength(encryptedText.trim(), "The encryptedText must be not empty.");
        byte[] encryptedBytes = decode(encryptedText);

        Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);

        try {

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);

        } catch (Exception e) {
            throw new SecurityException("Error while decrypting with RSA algorithm.", e);
        }
    }

    private static Cipher getCipher(Key key, int mode) {

        validateKey(key);

        try {

            Cipher decryptCipher = Cipher.getInstance(ALGORITHM);
            decryptCipher.init(mode, key);

            return decryptCipher;

        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(ERROR_KEY_INVALID, e);
        } catch (Exception e) {
            throw new RSAServerException("Error while loading RSA algorithm server data. This is a server error.", e);
        }

    }

    private static void validateKey(Key key) {

        Assert.notNull(key, "The key must be not null.");

        if (!key.getAlgorithm().equals(ALGORITHM))
            throw new IllegalArgumentException(ERROR_KEY_ALGORITHM_IS_DIFFERENT);

        if (key instanceof PrivateKey privateKey) {
            if (privateKey.isDestroyed())
                throw new IllegalArgumentException(ERROR_KEY_DESTROYED);
        }

    }

    private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(ALGORITHM);
    }

    private static PKCS8EncodedKeySpec getPkcs8EncodedKeySpec(String cleanedKey) {

        byte[] decoded = decode(cleanedKey);
        return new PKCS8EncodedKeySpec(decoded);

    }

    private static X509EncodedKeySpec getX509EncodedKeySpec(String cleanedKey) {

        byte[] decoded = decode(cleanedKey);
        return new X509EncodedKeySpec(decoded);

    }

    private static byte[] decode(String text) {
        return Base64.getDecoder().decode(text);
    }

    private static byte[] encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }
}
