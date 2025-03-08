package fr.karspa.hiker_thinker.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomGenerator {

    public static String generateRandomString(int length, boolean specialChars) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (specialChars) {
            chars += "!@#$%^&*()-_=+[]{}<>?";
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String generateRandomString(int length) {
        return generateRandomString(length, false);
    }

    public static String generateRandomString() {
        return generateRandomString(10, false);
    }
}
