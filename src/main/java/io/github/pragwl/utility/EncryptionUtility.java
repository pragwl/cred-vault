package io.github.pragwl.utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import lombok.extern.slf4j.Slf4j;

/** Utility class for encryption-related functions. */
@Slf4j
public final class EncryptionUtility {

    private EncryptionUtility() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a random salt.
     *
     * @param length The length of the salt in bytes.
     * @return The generated salt as a byte array.
     * @throws IllegalArgumentException if the length is invalid.
     */
    public static byte[] generateSalt(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Salt length must be positive.");
        }
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Generates a random encryption key.
     *
     * @param keySize The size of the encryption key in bits.
     * @return The generated encryption key as a Base64 encoded string.
     * @throws IllegalArgumentException if the key size is invalid.
     */
    public static String generateEncryptionKey(int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize, new SecureRandom());
            return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            log.error("AES algorithm not found: {}", e.getMessage());
            throw new IllegalStateException("AES algorithm not found.", e);
        }
    }
}