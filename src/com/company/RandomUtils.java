package com.company;

import java.util.Random;

public class RandomUtils {
    public static int getRandomIntInRange(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("to must be greater than from");
        } else if (from == to) {
            return from;
        }

        Random random = new Random();
        return random.nextInt((to - from) + 1) + from;
    }

    public static double getRandomDoubleInRange(double from, double to) {
        if (from > to) {
            throw new IllegalArgumentException("to must be greater than from");
        } else if (from == to) {
            return from;
        }

        Random random = new Random();
        return random.nextDouble() * (to - from) + from;
    }

    public static int getRandomElement(int[] array) {
        int randomIndex = new Random().nextInt(array.length);

        return array[randomIndex];
    }
}
