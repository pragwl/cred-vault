package io.github.pragwl.manager;

import io.github.pragwl.domain.Account;
import io.github.pragwl.domain.Password;

/**
 * Abstract base class for account managers, providing common functionalities for creating,
 * editing, and deleting accounts.
 */
public abstract class AccountManager {

    /**
     * Adds a new account.
     *
     * @param account The account to add.
     * @return The added account.
     */
    public abstract Account addAccount(Account account);

    /**
     * Creates a new `Account` instance.
     *
     * @param accountName     The name of the account.
     * @param accountId       The unique identifier of the account.
     * @param accountPassword The initial password for the account.
     * @return A new `Account` instance.
     */
    public Account createAccount(String accountName, String accountId, String accountPassword) {
        return Account.newAccount(accountName, accountId, accountPassword);
    }

    /**
     * Edits the account ID of an existing account.
     *
     * @param account   The account to edit.
     * @param accountId The new account ID.
     * @return A new `Account` instance with the updated account ID.
     * @throws IllegalArgumentException if the accountId is blank.
     */
    public Account editAccountId(Account account, String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be blank.");
        }

        Account updatedAccount = Account.cloneAccount(account);
        updatedAccount.setId(accountId);
        return updatedAccount;
    }

    /**
     * Edits the password of an existing account.
     *
     * @param account         The account to edit.
     * @param accountPassword The new password.
     * @return A new `Account` instance with the updated password.
     */
    public Account editAccountPassword(Account account, String accountPassword) {
        Account updatedAccount = Account.cloneAccount(account);
        Password newPassword = Password.createNewPassword(accountPassword);
        updatedAccount.setPassword(newPassword);
        return updatedAccount;
    }

    /**
     * Deletes an account.
     *
     * @param account The account to delete.
     */
    public abstract void deleteAccount(Account account);

    /**
     * Displays a list of accounts.
     */
    public abstract void viewAccounts();

    /**
     * Retrieves an account by its index.
     *
     * @param index The index of the account to retrieve.
     * @return The account at the specified index, or `null` if the index is out of bounds.
     */
    public abstract Account getAccountByIdx(int index);

    /**
     * Checks if there are any accounts.
     *
     * @return `true` if there are accounts, `false` otherwise.
     */
    public abstract boolean hasAccounts();
}
