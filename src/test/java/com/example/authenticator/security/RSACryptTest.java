package com.example.authenticator.security;

import com.example.authenticator.exceptions.RSAServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RSACryptTest {

    @ParameterizedTest
    @ValueSource(strings = { """
                -----BEGIN PRIVATE KEY-----
                MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMxXIRbeYPjYTVe4
                jp+4Fq8x8T34UkI/NE13/0UX1iXFrhvRdjEnw1hRGUrKPVHv5muuqQg4+VfBYRPr
                bevGKLq+H85OpROuJ44f2gkbZuIyXIIfyxQxLUFuivHf8C7Ptpiz3pSQzkiQiHoG
                MANB+OIQOVPxNBKdYqGQ3+nIahPXAgMBAAECgYEAka68xJUfBcsQuS23YV/ZrGYq
                3EeiPeDZ5TFcKWJpJtzc2LBpV3pF5z1KjuvI9BXZbYREtrwH6OzRzWXYJHROrCyp
                8rIArrj/uRPQfQqBg2DMliaiuowsuzhXy/CyGWZwSCjNb6isbglWR3bikaRPes/H
                J6VTPlDfRDDm8utCSiECQQD/bkAycy7JEfk5tQFkApngsqt8lnd0wI2nmRQDUHBR
                fscnjXQdcGrMOb33QPtSZtao3ZF4IiUvjzvWOG1mGjGVAkEAzMu565xV+MuVAtiP
                joxg2ifyzcLthi/Hft5hv8sasL85DRYPkFbLZD+Qe6tfpkcH943zRu615LYGMleD
                /6lsuwJBAOlN9m0eL9mQBSfkYETM62gFSgUeGjYVuk0e0NzGGBEXzygdV7Wb/LBU
                /WJdhDCbpe3PkxM7fOOX3HuqRqI/wpECQCleBYIYJC2LQeA/SlAq/u1SIHN7qmru
                f8eat/f72LpXBfkkuaQYRCFNzauNZFa8Bln07G0pjshSFFJa5yjfWj0CQDGkjX0Q
                IWmE0ybVSFXyl8V0AaJ7/l0N31eIUiru+tYihb98TfGMBwPEWivekH62RZXyz/RR
                KJb7z6HZLg4r3Tk=
                -----END PRIVATE KEY-----""",
                """
                MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMxXIRbeYPjYTVe4
                jp+4Fq8x8T34UkI/NE13/0UX1iXFrhvRdjEnw1hRGUrKPVHv5muuqQg4+VfBYRPr
                bevGKLq+H85OpROuJ44f2gkbZuIyXIIfyxQxLUFuivHf8C7Ptpiz3pSQzkiQiHoG
                MANB+OIQOVPxNBKdYqGQ3+nIahPXAgMBAAECgYEAka68xJUfBcsQuS23YV/ZrGYq
                3EeiPeDZ5TFcKWJpJtzc2LBpV3pF5z1KjuvI9BXZbYREtrwH6OzRzWXYJHROrCyp
                8rIArrj/uRPQfQqBg2DMliaiuowsuzhXy/CyGWZwSCjNb6isbglWR3bikaRPes/H
                J6VTPlDfRDDm8utCSiECQQD/bkAycy7JEfk5tQFkApngsqt8lnd0wI2nmRQDUHBR
                fscnjXQdcGrMOb33QPtSZtao3ZF4IiUvjzvWOG1mGjGVAkEAzMu565xV+MuVAtiP
                joxg2ifyzcLthi/Hft5hv8sasL85DRYPkFbLZD+Qe6tfpkcH943zRu615LYGMleD
                /6lsuwJBAOlN9m0eL9mQBSfkYETM62gFSgUeGjYVuk0e0NzGGBEXzygdV7Wb/LBU
                /WJdhDCbpe3PkxM7fOOX3HuqRqI/wpECQCleBYIYJC2LQeA/SlAq/u1SIHN7qmru
                f8eat/f72LpXBfkkuaQYRCFNzauNZFa8Bln07G0pjshSFFJa5yjfWj0CQDGkjX0Q
                IWmE0ybVSFXyl8V0AaJ7/l0N31eIUiru+tYihb98TfGMBwPEWivekH62RZXyz/RR
                KJb7z6HZLg4r3Tk=
                """,
                "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMxXIRbeYPjYTVe4" +
                "jp+4Fq8x8T34UkI/NE13/0UX1iXFrhvRdjEnw1hRGUrKPVHv5muuqQg4+VfBYRPr" +
                "bevGKLq+H85OpROuJ44f2gkbZuIyXIIfyxQxLUFuivHf8C7Ptpiz3pSQzkiQiHoG" +
                "MANB+OIQOVPxNBKdYqGQ3+nIahPXAgMBAAECgYEAka68xJUfBcsQuS23YV/ZrGYq" +
                "3EeiPeDZ5TFcKWJpJtzc2LBpV3pF5z1KjuvI9BXZbYREtrwH6OzRzWXYJHROrCyp" +
                "8rIArrj/uRPQfQqBg2DMliaiuowsuzhXy/CyGWZwSCjNb6isbglWR3bikaRPes/H" +
                "J6VTPlDfRDDm8utCSiECQQD/bkAycy7JEfk5tQFkApngsqt8lnd0wI2nmRQDUHBR" +
                "fscnjXQdcGrMOb33QPtSZtao3ZF4IiUvjzvWOG1mGjGVAkEAzMu565xV+MuVAtiP" +
                "joxg2ifyzcLthi/Hft5hv8sasL85DRYPkFbLZD+Qe6tfpkcH943zRu615LYGMleD" +
                "/6lsuwJBAOlN9m0eL9mQBSfkYETM62gFSgUeGjYVuk0e0NzGGBEXzygdV7Wb/LBU" +
                "/WJdhDCbpe3PkxM7fOOX3HuqRqI/wpECQCleBYIYJC2LQeA/SlAq/u1SIHN7qmru" +
                "f8eat/f72LpXBfkkuaQYRCFNzauNZFa8Bln07G0pjshSFFJa5yjfWj0CQDGkjX0Q" +
                "IWmE0ybVSFXyl8V0AaJ7/l0N31eIUiru+tYihb98TfGMBwPEWivekH62RZXyz/RR" +
                "KJb7z6HZLg4r3Tk="
    })
    @DisplayName("Returns a valid private key when a valid private key PEM is provided to readRSAPrivateKeyFromPEM() method")
    void shouldReturnPrivateKeyWhenValidPrivateKeyPEMIsProvided(String privateKeyPEM) {

        var privateKey = RSACrypt.readRSAPrivateKeyFromPEM(privateKeyPEM);

        Assertions.assertNotNull(privateKey);
        Assertions.assertEquals("RSA", privateKey.getAlgorithm());
        Assertions.assertFalse(privateKey.isDestroyed());

    }

    @Test
    @DisplayName("Throw an error when an invalid private key PEM is provided to readRSAPrivateKeyFromPEM() method")
    void shouldThrowErrorWhenInvalidPrivateKeyPEMIsProvided() {

        Assertions.assertThrows(RSAServerException.class, () -> RSACrypt.readRSAPrivateKeyFromPEM("invalid"), RSACrypt.ERROR_WHILE_LOADING_PRIVATE_KEY);

    }

    @Test
    @DisplayName("Throw an error when an empty private key PEM is provided to readRSAPrivateKeyFromPEM() method")
    void shouldThrowErrorForAnEmptyPrivateKeyPEM() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPrivateKeyFromPEM(""));

    }

    @Test
    @DisplayName("Throw an error when a blank private key PEM is provided to readRSAPrivateKeyFromPEM() method")
    void shouldThrowErrorForABlankPrivateKeyPEM() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPrivateKeyFromPEM("    "));
    }

    @Test
    @DisplayName("Throw an error when a null private key PEM is provided to readRSAPrivateKeyFromPEM() method")
    void shouldThrowErrorForANullPrivateKeyPEM() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPrivateKeyFromPEM(null));

    }

    @Test
    @DisplayName("Returns a valid public key when a public key PEM w/ header and footer and breaklines is provided to readRSAPublicKeyFromPEM() method")
    void shouldReturnPublicKeyWhenValidPublicKeyPEMIsProvided() {

        var publicKey = RSACrypt.readRSAPublicKeyFromPEM("""
                -----BEGIN PUBLIC KEY-----
                MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMVyEW3mD42E1XuI6fuBavMfE9
                +FJCPzRNd/9FF9Ylxa4b0XYxJ8NYURlKyj1R7+ZrrqkIOPlXwWET623rxii6vh/O
                TqUTrieOH9oJG2biMlyCH8sUMS1Bborx3/Auz7aYs96UkM5IkIh6BjADQfjiEDlT
                8TQSnWKhkN/pyGoT1wIDAQAB
                -----END PUBLIC KEY-----""");

        Assertions.assertNotNull(publicKey);
        Assertions.assertEquals("RSA", publicKey.getAlgorithm());

    }

    @Test
    @DisplayName("Returns a valid public key when a public key PEM w/ breaklines is provided to readRSAPublicKeyFromPEM() method")
    void shouldReturnPublicKeyWhenValidPublicKeyPEMIsProvidedWithoutHeaderAndFooter() {

        var publicKey = RSACrypt.readRSAPublicKeyFromPEM(
                """
                        MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMVyEW3mD42E1XuI6fuBavMfE9
                        +FJCPzRNd/9FF9Ylxa4b0XYxJ8NYURlKyj1R7+ZrrqkIOPlXwWET623rxii6vh/O
                        TqUTrieOH9oJG2biMlyCH8sUMS1Bborx3/Auz7aYs96UkM5IkIh6BjADQfjiEDlT
                        8TQSnWKhkN/pyGoT1wIDAQAB
                        """);

        Assertions.assertNotNull(publicKey);
        Assertions.assertEquals("RSA", publicKey.getAlgorithm());

    }

    @Test
    @DisplayName("Returns a valid public key when a public key PEM is provided to readRSAPublicKeyFromPEM() method")
    void shouldReturnPublicKeyWhenValidPublicKeyPEMIsProvidedWithoutHeaderAndFooterAndBreaklines() {

        var publicKey = RSACrypt.readRSAPublicKeyFromPEM(
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMVyEW3mD42E1XuI6fuBavMfE9" +
                        "+FJCPzRNd/9FF9Ylxa4b0XYxJ8NYURlKyj1R7+ZrrqkIOPlXwWET623rxii6vh/O" +
                        "TqUTrieOH9oJG2biMlyCH8sUMS1Bborx3/Auz7aYs96UkM5IkIh6BjADQfjiEDlT" +
                        "8TQSnWKhkN/pyGoT1wIDAQAB");

        Assertions.assertNotNull(publicKey);
        Assertions.assertEquals("RSA", publicKey.getAlgorithm());

    }

    @Test
    @DisplayName("Throw an error when an invalid public key PEM is provided to readRSAPublicKeyFromPEM() method")
    void shouldThrowErrorWhenInvalidPublicKeyIsProvided() {

        Assertions.assertThrows(RSAServerException.class, () -> RSACrypt.readRSAPublicKeyFromPEM("invalid"), RSACrypt.ERROR_WHILE_LOADING_PUBLIC_KEY);

    }

    @Test
    @DisplayName("Throw an error when an empty public key PEM is provided to readRSAPublicKeyFromPEM() method")
    void shouldThrowErrorForAnEmptyPublicKey() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPublicKeyFromPEM(""));

    }

    @Test
    @DisplayName("Throw an error when a blank public key PEM is provided to readRSAPublicKeyFromPEM() method")
    void shouldThrowErrorForABlankPublicKey() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPublicKeyFromPEM("     "));

    }

    @Test
    @DisplayName("Throw an error when a null public key PEM is provided to readRSAPublicKeyFromPEM() method")
    void shouldThrowErrorForANullPublicKey() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPublicKeyFromPEM(null));

    }

    @Test
    @DisplayName("Throw an error when a null private key is provided to readRSAPublicKeyFromPrivateKey() method")
    void shouldThrowErrorForANullPrivateKey() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPublicKeyFromPrivateKey(null));

    }

    @Test
    @DisplayName("Throw an error when a destroyed private key is provided to readRSAPublicKeyFromPrivateKey() method")
    void shouldThrowErrorForADestroyedPrivateKey() {

        PrivateKey destroyed = Mockito.mock(PrivateKey.class);
        when(destroyed.isDestroyed()).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.readRSAPublicKeyFromPrivateKey(destroyed));

    }

    @Test
    @DisplayName("Generates a key pair, and test if some text encrypted with the public key is decrypted by the equivalent private key.")
    void shouldEncryptPlainTextWithPublicKey() {

        var keyPair = RSACrypt.generateKeyPair();
        var plainText = "some text";
        var encryptedText = RSACrypt.encrypt(plainText, keyPair.getPublic());

        Assertions.assertNotNull(keyPair);
        Assertions.assertNotNull(encryptedText);
        Assertions.assertEquals(plainText, RSACrypt.decrypt(encryptedText, keyPair.getPrivate()));


    }

    @Test
    @DisplayName("Generates a key pair, and test if some text encrypted with the private key is decrypted by the equivalent public key (sign).")
    void shouldSignPlainText() {

        var keyPair = RSACrypt.generateKeyPair();
        var plainText = "some text";
        var encryptedText = RSACrypt.encrypt(plainText, keyPair.getPrivate());

        Assertions.assertNotNull(keyPair);
        Assertions.assertNotNull(encryptedText);
        Assertions.assertEquals(plainText, RSACrypt.decrypt(encryptedText, keyPair.getPublic()));

    }

    @Test
    @DisplayName("Throw an error when a destroyed private key is provided to encrypt() method")
    void shouldThrowErrorForADestroyedPrivateKeyWhenEncrypt() {

        PrivateKey privateKey = Mockito.mock(PrivateKey.class);
        when(privateKey.isDestroyed()).thenReturn(true);
        when(privateKey.getAlgorithm()).thenReturn(RSACrypt.ALGORITHM);
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.encrypt("some text", privateKey), RSACrypt.ERROR_KEY_DESTROYED);

    }

    @Test
    @DisplayName("Throw an error when a private key of other algorithm is provided to encrypt() method")
    void shouldThrowErrorForPrivateKeyOfOtherAlgWhenEncrypt() {

        PrivateKey privateKey = Mockito.mock(PrivateKey.class);
        when(privateKey.getAlgorithm()).thenReturn("OTHER");
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.encrypt("some text", privateKey), RSACrypt.ERROR_KEY_ALGORITHM_IS_DIFFERENT);

    }

    @Test
    @DisplayName("Throw an error when a destroyed private key is provided to decrypt() method")
    void shouldThrowErrorForADestroyedPrivateKeyWhenDecrypt() {

        PrivateKey privateKey = Mockito.mock(PrivateKey.class);
        when(privateKey.isDestroyed()).thenReturn(true);
        when(privateKey.getAlgorithm()).thenReturn(RSACrypt.ALGORITHM);
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.decrypt("c29tZSB0ZXh0", privateKey), RSACrypt.ERROR_KEY_DESTROYED);

    }

    @Test
    @DisplayName("Throw an error when a private key of other algorithm is provided to decrypt() method")
    void shouldThrowErrorForPrivateKeyOfOtherAlgWhenDecrypt() {

        PrivateKey privateKey = Mockito.mock(PrivateKey.class);
        when(privateKey.getAlgorithm()).thenReturn("OTHER");
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.decrypt("c29tZSB0ZXh0", privateKey), RSACrypt.ERROR_KEY_ALGORITHM_IS_DIFFERENT);

    }

    @Test
    @DisplayName("Throw an error when an invalid private key is provided to decrypt() method")
    void shouldThrowErrorForAnInvalidPrivateKeyWhenDecrypt() {

        PrivateKey privateKey = Mockito.mock(PrivateKey.class);
        when(privateKey.isDestroyed()).thenReturn(false);
        when(privateKey.getAlgorithm()).thenReturn(RSACrypt.ALGORITHM);
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.decrypt("c29tZSB0ZXh0", privateKey), RSACrypt.ERROR_KEY_INVALID);

    }

    @Test
    @DisplayName("Throw an error when an invalid private key is provided to encrypt() method")
    void shouldThrowErrorForAnInvalidPrivateKeyWhenEncrypt() {

        PrivateKey privateKey = Mockito.mock(PrivateKey.class);
        when(privateKey.isDestroyed()).thenReturn(false);
        when(privateKey.getAlgorithm()).thenReturn(RSACrypt.ALGORITHM);
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.encrypt("some text", privateKey), RSACrypt.ERROR_KEY_INVALID);

    }

    @Test
    @DisplayName("Throw an error when an invalid public key is provided to decrypt() method")
    void shouldThrowErrorForAnInvalidPublicKeyWhenDecrypt() {

        PublicKey publicKey = Mockito.mock(PublicKey.class);
        when(publicKey.getAlgorithm()).thenReturn(RSACrypt.ALGORITHM);
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.decrypt("c29tZSB0ZXh0", publicKey), RSACrypt.ERROR_KEY_INVALID);

    }

    @Test
    @DisplayName("Throw an error when an invalid public key is provided to encrypt() method")
    void shouldThrowErrorForAnInvalidPublicKeyWhenEncrypt() {

        PublicKey publicKey = Mockito.mock(PublicKey.class);
        when(publicKey.getAlgorithm()).thenReturn(RSACrypt.ALGORITHM);
        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.encrypt("some text", publicKey), RSACrypt.ERROR_KEY_INVALID);

    }

    @Test
    @DisplayName("Throw an error when a null plain text is provided to encrypt() method")
    void shouldThrowErrorForANullPlainTextWhenEncrypt() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.encrypt(null, null));

    }

    @Test
    @DisplayName("Throw an error when a null plain text is provided to decrypt() method")
    void shouldThrowErrorForANullEncryptedTextWhenDecrypt() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.decrypt(null, null));

    }

    @ParameterizedTest
    @ValueSource(strings = { "", "    " })
    @DisplayName("Throw an error when an invalid plain text is provided to encrypt() method")
    void shouldThrowErrorForAnInvalidPlainTextWhenEncrypt(String plainText) {

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.encrypt(plainText, null));

    }

    @ParameterizedTest
    @ValueSource(strings = { "", "    " })
    @DisplayName("Throw an error when an blank plain text is provided to decrypt() method")
    void shouldThrowErrorForABlankEncryptedTextWhenDecrypt(String plainText) {

        var keyPair = RSACrypt.generateKeyPair();

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.decrypt(plainText, keyPair.getPublic()));

    }

    @Test
    @DisplayName("Throw an error when an invalid base 64 plain text is provided to decrypt() method")
    void shouldThrowErrorForAnInvalidBase64EncryptedTextWhenDecrypt() {

        var keyPair = RSACrypt.generateKeyPair();

        Assertions.assertThrows(IllegalArgumentException.class, () -> RSACrypt.decrypt("not_base64_text", keyPair.getPublic()));

    }
}
