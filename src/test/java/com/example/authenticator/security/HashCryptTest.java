package com.example.authenticator.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class HashCryptTest {

    //The SHA256 value of "teste"
    public static final String SHA_256 = "46070d4bf934fb0d4b06d9e2c46e346944e322444900a435d7d9a95e6d7435f5";

    @Test
    @DisplayName("Returns the hash value when a valid string is provided to hash() method")
    void shouldReturnTheHashValueForANonEmptyString(){

        var hash = HashCrypt.hash("teste");

        Assertions.assertEquals(SHA_256, hash);

    }

    @Test
    @DisplayName("Throw an error when an EMPTY plain text is provided to hash() method")
    void shouldThrowErrorForAEmptyString(){

        Assertions.assertThrows(IllegalArgumentException.class, () ->  HashCrypt.hash(""));

    }

    @Test
    @DisplayName("Throw an error when a BLANK plain text is provided to hash() method")
    void shouldThrowErrorForABlankString(){

        Assertions.assertThrows(IllegalArgumentException.class, () ->  HashCrypt.hash("       "));

    }

    @Test
    @DisplayName("Throw an error when a NULL plain text is provided to hash() method")
    void shouldThrowErrorForANullString(){

        Assertions.assertThrows(IllegalArgumentException.class, () ->  HashCrypt.hash(null));

    }
    @ParameterizedTest
    @CsvSource(value = {
            "teste, 46070d4bf934fb0d4b06d9e2c46e346944e322444900a435d7d9a95e6d7435f5",
            "Another string to test, 5de1bfd07bc9ededf3fd7e033d4b4e2e47146cbbb3c8840a272333d430ccd95b",
            "Yet another test case, edcd9c633903cc427d4ed5daffe727de6d18747e2a1e006366fc00479e4d94ed"},
            delimiter = ','
    )
    @DisplayName("Returns TRUE when a match string is provided to matches() method")
    void shouldReturnTrueForAMatchString(String plainText, String hash){

        Assertions.assertTrue(HashCrypt.matches(plainText, hash));

    }

    @ParameterizedTest
    @ValueSource(strings = {"This is not a match", "Another non-matching string", "Completely different text"})
    @DisplayName("Returns FALSE when a non match string is provided to matches() method")
    void shouldReturnFalseForANonMatchString(String plainText){

        Assertions.assertFalse(HashCrypt.matches(plainText, SHA_256));

    }
}
