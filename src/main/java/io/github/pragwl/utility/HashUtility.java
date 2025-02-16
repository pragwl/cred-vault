package io.github.pragwl.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;

/** Utility class for generating hash values. */
@Slf4j
public final class HashUtility {

    private HashUtility() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates an SHA-256 hash of the given string.
     *
     * @param input The string to hash.
     * @return The SHA-256 hash of the input string, or `null` if an error occurs.
     */
    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found: {}", e.getMessage());
            return null;
        }
    }
}