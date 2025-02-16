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
 * Manages active accounts. This class implements the Singleton pattern.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveAccountsManager extends AccountManager {

    private static ActiveAccountsManager activeAccountsManager;
    private final TreeSet<Account> activeAccounts = initializeActiveAccounts();

    /**
     * Retrieves the singleton instance of `ActiveAccountsManager`.
     *
     * @return The singleton instance.
     */
    public static ActiveAccountsManager getInstance() {
        if (activeAccountsManager == null) {
            synchronized (ActiveAccountsManager.class) {
                if (activeAccountsManager == null) {
                    activeAccountsManager = new ActiveAccountsManager();
                }
            }
        }
        return activeAccountsManager;
    }

    private TreeSet<Account> initializeActiveAccounts() {
        List<String> activeAccountNames =
                FileUtility.getFilesListFromDirectory(AccountConfig.activeAccountDirectory);
        return activeAccountNames.stream()
                .map(
                        accountName ->
                                (Account)
                                        SerializationUtil.deserializeObject(
                                                AccountConfig.activeAccountDirectory, accountName))
                .collect(
                        Collectors.toCollection(
                                () ->
                                        new TreeSet<>(
                                                Comparator.comparing(Account::getCreatedOn)
                                                        .thenComparing(Account::getVersion))));
    }

    /**
     * Adds an account to the active accounts.
     *
     * @param account The account to add.
     * @return The added account.
     */
    @Override
    public Account addAccount(Account account) {
        activeAccounts.add(account);
        return account;
    }

    /**
     * Deletes an account from the active accounts.
     *
     * @param account The account to delete.
     */
    @Override
    public void deleteAccount(Account account) {
        activeAccounts.remove(account);
    }

    /** Displays a table of active accounts. */
    @Override
    public void viewAccounts() {
        ConsolePrinter.printTable(List.of(activeAccounts.toArray()), List.of("serialVersionUID"));
    }

    /**
     * Retrieves an active account by its index.
     *
     * @param index The index of the account to retrieve.
     * @return The account at the specified index, or `null` if the index is out of bounds.
     */
    @Override
    public Account getAccountByIdx(int index) {
        if (index < 0 || index >= activeAccounts.size()) {
            return null;
        }
        return Utility.getValueAtIndex(activeAccounts, index);
    }

    /**
     * Checks if there are any active accounts.
     *
     * @return `true` if there are active accounts, `false` otherwise.
     */
    @Override
    public boolean hasAccounts() {
        return !activeAccounts.isEmpty();
    }

    /**
     * Configuration class for active accounts. Defines the directory where active accounts are
     * stored.
     */
    public static class AccountConfig {
        public static final String activeAccountDirectory = "accounts/";
    }
}
