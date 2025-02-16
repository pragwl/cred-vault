package io.github.pragwl.utility;

import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/** Utility class for encrypting and decrypting data using AES encryption. */
public class Encryptor {

    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;

    private Encryptor() {
        // Private constructor to prevent instantiation
    }

    /**
     * Encrypts the given byte array using AES encryption.
     *
     * @param inputBytes The byte array to encrypt.
     * @param salt The salt to use for encryption.
     * @param encryptionKey The encryption key to use.
     * @return The encrypted byte array, or `null` if an error occurs.
     */
    public static byte[] encrypt(byte[] inputBytes, byte[] salt, String encryptionKey) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
            KeySpec keySpec =
                    new PBEKeySpec(encryptionKey.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), ENCRYPTION_ALGORITHM);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            return Base64.getEncoder().encode(cipher.doFinal(inputBytes));
        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage()); // Log the exception message
            System.err.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    /**
     * Decrypts the given byte array using AES decryption.
     *
     * @param encryptedBytes The byte array to decrypt.
     * @param salt The salt to use for decryption.
     * @param encryptionKey The encryption key to use.
     * @return The decrypted byte array, or `null` if an error occurs.
     */
    public static byte[] decrypt(byte[] encryptedBytes, byte[] salt, String encryptionKey) {
        try {

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
            KeySpec keySpec =
                    new PBEKeySpec(encryptionKey.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), ENCRYPTION_ALGORITHM);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            return cipher.doFinal(Base64.getDecoder().decode(encryptedBytes));
        } catch (Exception e) {
            System.err.println("Decryption failed: " + e.getMessage()); // Log the exception message
            System.err.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
}
