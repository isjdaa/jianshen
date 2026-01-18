package com.hello.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt helper for password hashing and verification.
 * Requires dependency: org.mindrot:jbcrypt:0.4
 */
public class BCryptUtil {

    // Hash a plain-text password
    public static String hashPassword(String plain) {
        if (plain == null) return null;
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    // Verify plain password vs hashed password
    public static boolean verifyPassword(String plain, String hashed) {
        if (plain == null || hashed == null) return false;
        try {
            return BCrypt.checkpw(plain, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}