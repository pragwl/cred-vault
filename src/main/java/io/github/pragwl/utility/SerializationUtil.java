package io.github.pragwl.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

/** Utility class for serializing and deserializing objects. */
@Slf4j
public final class SerializationUtil {

    public static final String fileExtension = ".ser";
    private static final byte[] SALT = {
            0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
    };
    private static final String ENCRYPTION_KEY;
    private static final String ENCRYPTION_KEY_DIR = "config/encflekey.txt";

    static {
        try {
            Path path = Paths.get(ENCRYPTION_KEY_DIR);
            if (Files.exists(path)) {
                ENCRYPTION_KEY = Files.readString(path);
                log.info("Encryption key loaded from: {}", ENCRYPTION_KEY_DIR);
            } else {
                log.error("Encryption key file not found: {}", ENCRYPTION_KEY_DIR);
                throw new IllegalStateException(ENCRYPTION_KEY_DIR + " is required.");
            }
        } catch (IOException e) {
            log.error("Failed to read encryption key: {}", e.getMessage());
            throw new IllegalStateException("Failed to read encryption key: " + e.getMessage(), e); // Include the exception
        }
    }

    private SerializationUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Serializes an object to a file, encrypting the data before saving.
     *
     * @param obj The object to serialize.
     * @param directoryPath The directory where the file will be saved.
     * @param fileName The name of the file.
     * @throws RuntimeException if serialization or encryption fails.
     */
    public static void serializeObject(Object obj, String directoryPath, String fileName) {
        try {
            // Create the directory if it doesn't exist
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean isCreated = directory.mkdirs();
                if (!isCreated) {
                    log.error("Failed to create directory: {}", directoryPath);
                    throw new IOException("Failed to create directory: " + directoryPath);
                }
                log.info("Created directory: {}", directoryPath);
            }

            // Serialize the object to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            byte[] serializedData = bos.toByteArray();

            // Encrypt the serialized data
            byte[] encryptedData = Encryptor.encrypt(serializedData, SALT, ENCRYPTION_KEY);

            if (encryptedData == null) {
                log.error("Encryption failed during serialization.");
                throw new RuntimeException("Encryption process is incomplete. Got an error.");
            }

            String filePath = directoryPath + fileName + fileExtension;

            // Save the encrypted data to a file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(encryptedData);
                log.info("Serialized and encrypted object to file: {}", filePath);
            }

        } catch (IOException e) {
            log.error("Serialization failed: {}", e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Serialization failed: " + e.getMessage(), e); // Include the exception
        }
    }

    /**
     * Deserializes an object from a file, decrypting the data before deserialization.
     *
     * @param directoryPath The directory where the file is located.
     * @param fileName The name of the file.
     * @return The deserialized object, or `null` if deserialization fails.
     * @throws RuntimeException if decryption or deserialization fails.
     */
    public static Object deserializeObject(String directoryPath, String fileName) {
        String filePath = directoryPath + fileName;

        try {
            // Read the encrypted data from the file
            byte[] encryptedData;
            try (FileInputStream fis = new FileInputStream(filePath)) {
                encryptedData = fis.readAllBytes();
            }

            // Decrypt the data
            byte[] decryptedData = Encryptor.decrypt(encryptedData, SALT, ENCRYPTION_KEY);

            if (decryptedData == null) {
                log.error("Decryption failed during deserialization.");
                throw new RuntimeException("Decryption process is incomplete. Got an error.");
            }

            // Deserialize the decrypted byte array
            ByteArrayInputStream bis = new ByteArrayInputStream(decryptedData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();

            log.info("Deserialized and decrypted object from file: {}", filePath);
            return obj;

        } catch (IOException | ClassNotFoundException e) {
            log.error("Deserialization failed: {}", e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
}