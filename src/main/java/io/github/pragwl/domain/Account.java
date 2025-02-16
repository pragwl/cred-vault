package io.github.pragwl.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents an account within the credential management system. This class encapsulates account
 * details such as name, ID, password, and timestamps.
 */
@Getter
@ToString
@Builder(access = AccessLevel.PRIVATE)
public class Account implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** The name associated with the account. Cannot be null or blank. */
    @NonNull private final String name;

    /** The unique identifier for the account. Can be modified. */
    @Setter @NonNull private String id;

    /** The password associated with the account. Can be modified. */
    @Setter private Password password;

    /** The timestamp indicating when the account was created. */
    @NonNull private final LocalDateTime createdOn;

    /** The timestamp indicating when the account was last updated. Can be null. */
    @Setter private LocalDateTime updateOn;

    /** The version number of the account, incremented on each update. */
    @Setter private Integer version;

    /**
     * Creates a new `Account` instance.
     *
     * @param name The name of the account. Must not be blank.
     * @param id The unique identifier of the account. Must not be blank.
     * @param passwordString The initial password for the account.
     * @return A new `Account` instance.
     * @throws IllegalArgumentException if name or id is blank.
     */
    public static Account newAccount(String name, String id, String passwordString) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Account name and ID must not be blank.");
        }

        return Account.builder()
                .name(name.trim())
                .id(id.trim())
                .password(Password.createNewPassword(passwordString))
                .createdOn(LocalDateTime.now())
                .version(1)
                .build();
    }

    /**
     * Creates a clone of an existing `Account` instance.
     *
     * @param account The `Account` instance to clone.
     * @return A new `Account` instance with the same data as the original.
     */
    public static Account cloneAccount(Account account) {
        return Account.builder()
                .name(account.getName())
                .id(account.getId())
                .password(account.getPassword())
                .createdOn(account.getCreatedOn())
                .updateOn(account.getUpdateOn())
                .version(account.getVersion())
                .build();
    }

    /**
     * Increments the version of the account.
     *
     * @throws IllegalStateException if version is null
     */
    public void incrementVersion() {
        if (version == null) {
            throw new IllegalStateException("Version is null, cannot increment.");
        }
        this.version = this.version + 1;
    }
}