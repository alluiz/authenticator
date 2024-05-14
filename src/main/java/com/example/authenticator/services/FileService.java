package com.example.authenticator.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

@Service
public class FileService {

    public String open(String filePath) throws FileNotFoundException {

        var fileReader = new FileReader(filePath);

        try (BufferedReader br = new BufferedReader(fileReader)) {

            return br.lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            throw new RuntimeException("Error while open file: %s".formatted(filePath), e);
        }

    }

}
