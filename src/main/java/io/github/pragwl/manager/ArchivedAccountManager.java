package io.github.pragwl.manager;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.github.pragwl.domain.Account;
import io.github.pragwl.utility.ConsolePrinter;
import io.github.pragwl.utility.FileUtility;
import io.github.pragwl.utility.SerializationUtil;
import io.github.pragwl.utility.Utility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Manages archived accounts. This class implements the Singleton pattern.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArchivedAccountManager extends AccountManager {

    private static ArchivedAccountManager archivedAccManager;
    private final TreeSet<Account> archivedAccounts = initializeArchivedAccounts();

    /**
     * Retrieves the singleton instance of `ArchivedAccountManager`.
     *
     * @return The singleton instance.
     */
    public static ArchivedAccountManager getInstance() {
        if (archivedAccManager == null) {
            synchronized (ArchivedAccountManager.class) {
                if (archivedAccManager == null) {
                    archivedAccManager = new ArchivedAccountManager();
                }
            }
        }
        return archivedAccManager;
    }

    private TreeSet<Account> initializeArchivedAccounts() {
        List<String> archivedAccountNames =
                FileUtility.getFilesListFromDirectory(AccountConfig.archiveAccountDirectory);
        return archivedAccountNames.stream()
                .map(
                        accountName ->
                                (Account)
                                        SerializationUtil.deserializeObject(
                                                AccountConfig.archiveAccountDirectory, accountName))
                .collect(
                        Collectors.toCollection(
                                () ->
                                        new TreeSet<>(
                                                Comparator.comparing(Account::getCreatedOn)
                                                        .thenComparing(Account::getVersion))));
    }

    /**
     * Adds an account to the archived accounts.
     *
     * @param account The account to add.
     * @return The added account.
     */
    @Override
    public Account addAccount(Account account) {
        archivedAccounts.add(account);
        return account;
    }

    /**
     * Deletes an account from the archived accounts.
     *
     * @param account The account to delete.
     */
    @Override
    public void deleteAccount(Account account) {
        archivedAccounts.remove(account);
    }

    /**
     * Displays a table of archived accounts.
     */
    @Override
    public void viewAccounts() {
        ConsolePrinter.printTable(
                List.of(archivedAccounts.toArray()), List.of("serialVersionUID"));
    }

    /**
     * Retrieves an archived account by its index.
     *
     * @param index The index of the account to retrieve.
     * @return The account at the specified index, or `null` if the index is out of bounds.
     */
    @Override
    public Account getAccountByIdx(int index) {
        if (index < 0 || index >= archivedAccounts.size()) {
            return null;
        }
        return Utility.getValueAtIndex(archivedAccounts, index);
    }

    /**
     * Checks if there are any archived accounts.
     *
     * @return `true` if there are archived accounts, `false` otherwise.
     */
    @Override
    public boolean hasAccounts() {
        return !archivedAccounts.isEmpty();
    }

    /**
     * Configuration class for archived accounts. Defines the directory where archived accounts are
     * stored.
     */
    public static class AccountConfig {
        public static final String archiveAccountDirectory = "archived/";
    }
}
