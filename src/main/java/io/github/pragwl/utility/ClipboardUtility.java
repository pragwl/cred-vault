package io.github.pragwl.utility;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Utility class for interacting with the system clipboard.
 */
public final class ClipboardUtility {

    private ClipboardUtility() {
        // Private constructor to prevent instantiation
    }

    /**
     * Copies the given text to the system clipboard.
     *
     * @param text The text to copy to the clipboard.
     */
    public static void copyToClipboard(String text) {
        // Get the system clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // Create a transferable object to hold the text
        StringSelection selection = new StringSelection(text);

        // Set the content of the clipboard to the transferable object
        clipboard.setContents(selection, null);
    }
}