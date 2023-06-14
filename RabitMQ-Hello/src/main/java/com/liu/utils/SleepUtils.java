package com.liu.utils;

/**
 * @author liushuaibiao
 * @date 2023/6/13 16:04
 */
public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
