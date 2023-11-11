package com.tunetown.utils;

import java.util.HashMap;
import java.util.Random;

public class OTPUtils {
    private static final HashMap<String, Integer> otpStore = new HashMap<>();

    public static boolean verifyOTP(String email, int inputOTP) {
        Integer generatedOTP = otpStore.get(email);
        if(generatedOTP != null) {
            if(inputOTP == generatedOTP) {
                deleteOTP(email);
                return true;
            }
        }
        return false;
    }

    public static int generateOTP(String email) {
        Random random = new Random();
        int newOTP = random.nextInt(900000) + 100000; // Generates a random number between 100000 and 999999
        otpStore.put(email, newOTP);
        return newOTP;
    }

    private static void deleteOTP(String email) {
        otpStore.remove(email);
    }
}
