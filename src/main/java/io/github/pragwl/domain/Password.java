package io.github.pragwl.domain;

import io.github.pragwl.utility.EncryptionUtility;
import io.github.pragwl.utility.Encryptor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Getter
public class Password implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NonNull
    private final String password;

    @NonNull
    private final byte[] salt;

    @NonNull
    private final String encryptionKey;

    private Password(String password, byte[] salt, String encryptionKey) {
        this.password = password;
        this.salt = salt;
        this.encryptionKey = encryptionKey;
    }

    public static Password createNewPassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password is required");
        }
        byte[] salt = EncryptionUtility.generateSalt(8);
        String encryptionKey = EncryptionUtility.generateEncryptionKey(256);
        String encryptedPassword =
                new String(
                        Objects.requireNonNull(
                                Encryptor.encrypt(password.trim().getBytes(), salt, encryptionKey)),
                        StandardCharsets.UTF_8);
        return new Password(encryptedPassword, salt, encryptionKey);
    }

    public String getDecryptedPassword() {
        return new String(
                Objects.requireNonNull(Encryptor.decrypt(this.password.getBytes(), salt, encryptionKey)),
                StandardCharsets.UTF_8);
    }

    // Modify this method
    @Override
    public String toString() {
        return "********"; // Masked password
    }
}