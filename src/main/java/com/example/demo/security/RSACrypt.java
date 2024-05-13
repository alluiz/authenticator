package com.example.demo.security;

import com.example.demo.exceptions.RSAServerException;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSACrypt {

        public static PrivateKey readRSAPrivateKeyFromPEM(String privateKeyPEM) {

            try {

                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

                privateKeyPEM = privateKeyPEM
                        .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                        .replaceAll(System.lineSeparator(), "")
                        .replace("-----END RSA PRIVATE KEY-----", "");

                byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

                return keyFactory.generatePrivate(keySpec);

            } catch (Exception e) {
                throw new RSAServerException("Error while loading private key from PEM file.", e);
            }

        }

        public static PublicKey readRSAPublicKeyFromPEM(String publicKeyPEM) {

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            try {

                publicKeyPEM = publicKeyPEM
                        .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                        .replaceAll(System.lineSeparator(), "")
                        .replace("-----END RSA PUBLIC KEY-----", "");

                byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

                return keyFactory.generatePublic(keySpec);

            } catch (Exception e) {
                throw new SecurityException("Error while loading public key.", e);
            }

        }

        public static KeyPair generateKeyPair()  {

            try {

                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048); // Key size (bits)

                return keyPairGenerator.generateKeyPair();

            } catch (Exception e) {
                throw new SecurityException("Error while generating RSA Key Pair", e);
            }
        }

        public static String encrypt(String plainText, AsymmetricKey key) {

            Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);

            try {

                byte[] encryptedBytes = Base64.getDecoder().decode(plainText);
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

                return new String(decryptedBytes);

            } catch (Exception e) {
                throw new SecurityException("Error while crypting with RSA algorithm.", e);
            }

        }

        public static String decrypt(String encryptedText, AsymmetricKey key) {

            Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);

            try {

                byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

                return new String(decryptedBytes);

            } catch (Exception e) {
                throw new SecurityException("Error while decrypting with RSA algorithm.", e);
            }
        }

        private static Cipher getCipher(AsymmetricKey key, int mode) {

            try {

                Cipher decryptCipher = Cipher.getInstance("RSA");
                decryptCipher.init(mode, key);

                return decryptCipher;

            } catch (Exception e) {
                throw new RSAServerException("Error while loading RSA algorithm server data. This is a server error.", e);
            }

        }
}
