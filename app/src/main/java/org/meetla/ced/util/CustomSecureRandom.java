package org.meetla.ced.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CustomSecureRandom {

    private byte[] seed;
    private byte[] currentState;

    public CustomSecureRandom(byte[] seed) {
        this.seed = seed;
        this.currentState = Arrays.copyOf(seed, seed.length);
    }

    // Генерация следующего случайного числа
    public byte[] nextBytes(int numBytes) throws NoSuchAlgorithmException {
        byte[] randomBytes = new byte[numBytes];
        int generatedBytes = 0;

        while (generatedBytes < numBytes) {
            // Используем SHA-256 для обновления состояния
            currentState = hash(currentState);

            int bytesToCopy = Math.min(currentState.length, numBytes - generatedBytes);
            System.arraycopy(currentState, 0, randomBytes, generatedBytes, bytesToCopy);
            generatedBytes += bytesToCopy;
        }

        return randomBytes;
    }

    // Простейшая функция для хэширования данных с использованием SHA-256
    private byte[] hash(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input);
    }

    // Устанавливаем новый seed
    public void setSeed(byte[] seed) {
        this.seed = seed;
        this.currentState = Arrays.copyOf(seed, seed.length);
    }
}
