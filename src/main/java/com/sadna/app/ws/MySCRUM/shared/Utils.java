package com.sadna.app.ws.MySCRUM.shared;


import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import java.util.Random;

/**
 * Utils class
 */
@Service
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * @param length desired length
     * @return new UserId
     */
    public String generateUserId(int length) {
        return generateRandomString(length);
    }

    /**
     * @param length desired length
     * @return new TaskId
     */
    public String generateTaskId(int length) {
        return generateRandomString(length);
    }

    /**
     * @param length desired length
     * @return new TeamId
     */
    public String generateTeamId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }
}