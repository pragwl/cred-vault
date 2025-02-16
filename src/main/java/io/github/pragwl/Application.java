package io.github.pragwl;

import static java.lang.System.out;

import io.github.pragwl.domain.Account;
import io.github.pragwl.manager.AccountManager;
import io.github.pragwl.manager.ActiveAccountsManager;
import io.github.pragwl.manager.ArchivedAccountManager;
import io.github.pragwl.utility.*;

import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

/**
 * Main application class for CredManager.
 */
@Slf4j
public class Application {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final ActiveAccountsManager activeAccManager = ActiveAccountsManager.getInstance();
    private static final ArchivedAccountManager archiveAccManager =
            ArchivedAccountManager.getInstance();

    public static void main(String[] args) {
        boolean exit = false;
        do {
            printMenu();
            out.print("Choice = ");
            try {
                int choice = SCANNER.nextInt();
                SCANNER.nextLine(); // Consume newline
                exit = processChoice(choice);
            } catch (InputMismatchException e) {
                out.println("Invalid input. Enter a number.");
                SCANNER.nextLine(); // Consume invalid input
                log.warn("Invalid input: Not a number", e);
            } catch (IllegalArgumentException e) {
                out.println("Invalid argument: " + e.getMessage());
                log.error("Illegal argument: ", e);
            } catch (Exception e) {
                out.println("An unexpected error occurred: " + e.getMessage());
                log.error("An unexpected error occurred: ", e);
            }
        } while (!exit);
        SCANNER.close();
        log.info("Exiting CredManager Application");
    }

    private static void printMenu() {
        out.println("MENU");
        out.println("1. Add Account");
        out.println("2. Update Account");
        out.println("3. Delete Account");
        out.println("4. View Active Accounts");
        out.println("5. View Archived Accounts");
        out.println("6. Copy active account on clipboard");
        out.println("7. Copy archived account on clipboard");
        out.println("8. Exit");
    }

    private static boolean processChoice(int choice) {
        switch (choice) {
            case 1:
                addAccount();
                break;
            case 2:
                updateAccount();
                break;
            case 3:
                deleteAccount();
                break;
            case 4:
                activeAccManager.viewAccounts();
                break;
            case 5:
                archiveAccManager.viewAccounts();
                break;
            case 6:
                copyClipboard(activeAccManager);
                break;
            case 7:
                copyClipboard(archiveAccManager);
                break;
            case 8:
                return true; // Exit
            default:
                throw new IllegalArgumentException("Invalid menu option: " + choice);
        }
        return false;
    }

    private static void addAccount() {
        out.println("Account Name: ");
        String accountName = SCANNER.nextLine();
        out.println("Account Id: ");
        String accountId = SCANNER.nextLine();
        out.println("Account Password: ");
        String accountPassword = SCANNER.nextLine();

        try {
            Account account = activeAccManager.createAccount(accountName, accountId, accountPassword);
            String fileName = Utility.getFileNameForAccountObject(account);
            SerializationUtil.serializeObject(
                    account, ActiveAccountsManager.AccountConfig.activeAccountDirectory, fileName);
            activeAccManager.addAccount(account);
            log.info("Account added successfully: {}", account.getId());
        } catch (Exception e) {
            out.println("Error adding account: " + e.getMessage());
            log.error("Error adding account: ", e);
        }
    }

    private static void updateAccount() {
        activeAccManager.viewAccounts();
        out.print("Choice: ");
        try {
            int accSelection = SCANNER.nextInt();
            SCANNER.nextLine(); // Consume newline
            Account originalAccountObj = activeAccManager.getAccountByIdx(accSelection - 1);

            if (originalAccountObj == null) {
                ConsolePrinter.printWarningMessage("Invalid Choice");
                log.warn("Invalid account selection: {}", accSelection);
                return;
            }

            out.println("1. Update Id");
            out.println("2. Update Password");
            out.print("Choice: ");
            int updateChoice = SCANNER.nextInt();
            SCANNER.nextLine(); // Consume newline

            Account newAccountObj;
            switch (updateChoice) {
                case 1:
                    out.print("Id: ");
                    String id = SCANNER.nextLine();
                    newAccountObj = activeAccManager.editAccountId(originalAccountObj, id);
                    break;
                case 2:
                    out.print("Password: ");
                    String password = SCANNER.nextLine();
                    newAccountObj = activeAccManager.editAccountPassword(originalAccountObj, password);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid update option: " + updateChoice);
            }
            updateAccountObjects(originalAccountObj, newAccountObj);

        } catch (InputMismatchException e) {
            out.println("Invalid input. Enter account/update choice as a number.");
            SCANNER.nextLine(); // Consume invalid input
            log.warn("Invalid input: Not a number", e);
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
            log.error("Illegal argument: ", e);
        } catch (Exception e) {
            out.println("An unexpected error occurred: " + e.getMessage());
            log.error("An unexpected error occurred: ", e);
        }
    }

    private static void updateAccountObjects(Account originalAccountObj, Account newAccountObj) {
        originalAccountObj.setUpdateOn(LocalDateTime.now());
        newAccountObj.incrementVersion();
        activeAccManager.deleteAccount(originalAccountObj);
        activeAccManager.addAccount(newAccountObj);
        archiveAccManager.addAccount(originalAccountObj);

        String fileName = Utility.getFileNameForAccountObject(newAccountObj);
        SerializationUtil.serializeObject(
                newAccountObj, ActiveAccountsManager.AccountConfig.activeAccountDirectory, fileName);

        String archivedFileName = Utility.getFileNameForAccountObject(originalAccountObj);
        SerializationUtil.serializeObject(
                originalAccountObj,
                ArchivedAccountManager.AccountConfig.archiveAccountDirectory,
                archivedFileName);

        FileUtility.deleteFile(
                ActiveAccountsManager.AccountConfig.activeAccountDirectory
                        + Utility.getFileNameForAccountObject(originalAccountObj)
                        + SerializationUtil.fileExtension);
    }

    private static void deleteAccount() {
        activeAccManager.viewAccounts();
        out.print("Choice: ");
        try {
            int accSelection = SCANNER.nextInt();
            SCANNER.nextLine(); // Consume newline

            Account originalAccountObj = activeAccManager.getAccountByIdx(accSelection - 1);

            if (originalAccountObj == null) {
                ConsolePrinter.printWarningMessage("Invalid Choice");
                log.warn("Invalid account selection: {}", accSelection);
                return;
            }

            String fileName = Utility.getFileNameForAccountObject(originalAccountObj);
            activeAccManager.deleteAccount(originalAccountObj);
            FileUtility.deleteFile(
                    ActiveAccountsManager.AccountConfig.activeAccountDirectory
                            + fileName
                            + SerializationUtil.fileExtension);
            log.info("Account deleted successfully: {}", originalAccountObj.getId());

        } catch (InputMismatchException e) {
            out.println("Invalid input. Enter a number.");
            SCANNER.nextLine(); // Consume invalid input
            log.warn("Invalid input: Not a number", e);
        } catch (Exception e) {
            out.println("An unexpected error occurred: " + e.getMessage());
            log.error("An unexpected error occurred: ", e);
        }
    }

    private static void copyClipboard(AccountManager accountManager) {
        if (!accountManager.hasAccounts()) {
            ConsolePrinter.printWarningMessage("No accounts found.");
            log.warn("No accounts found.");
            return;
        }
        accountManager.viewAccounts();
        out.print("Choice: ");
        try {
            int accountChoice = SCANNER.nextInt();
            SCANNER.nextLine(); // Consume newline

            Account account = accountManager.getAccountByIdx(accountChoice - 1);

            if (account == null) {
                ConsolePrinter.printWarningMessage("Invalid Choice");
                log.warn("Invalid account selection: {}", accountChoice);
                return;
            }

            out.println("1. Copy Id");
            out.println("2. Copy Password");
            out.print("Choice: ");
            int copyChoice = SCANNER.nextInt();
            SCANNER.nextLine(); // Consume newline

            switch (copyChoice) {
                case 1:
                    ClipboardUtility.copyToClipboard(account.getId());
                    log.info("Account ID copied to clipboard for account: {}", account.getId());
                    break;
                case 2:
                    ClipboardUtility.copyToClipboard(account.getPassword().getDecryptedPassword());
                    log.info("Password copied to clipboard.");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid copy option: " + copyChoice);
            }
        } catch (InputMismatchException e) {
            out.println("Invalid input. Enter a number.");
            SCANNER.nextLine(); // Consume invalid input
            log.warn("Invalid input: Not a number", e);
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
            log.error("Illegal argument: ", e);
        } catch (Exception e) {
            out.println("An unexpected error occurred: " + e.getMessage());
            log.error("An unexpected error occurred: ", e);
        }
    }
}