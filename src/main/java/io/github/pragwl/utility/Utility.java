package io.github.pragwl.utility;


import java.util.Set;

import io.github.pragwl.domain.Account;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Utility class for general-purpose helper methods. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utility {

    /**
     * Retrieves a value at a specific index from a Set.
     *
     * @param set The Set to retrieve the value from.
     * @param index The index of the value to retrieve.
     * @param <T> The type of the elements in the Set.
     * @return The element at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public static <T> T getValueAtIndex(Set<T> set, int index) {
        if (index < 0 || index >= set.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        int i = 0;
        for (T element : set) {
            if (i == index) {
                return element;
            }
            i++;
        }

        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    /**
     * Generates a file name for an Account object using a hash of its properties.
     *
     * @param account The Account object.
     * @return A unique file name for the Account object.
     */
    public static String getFileNameForAccountObject(Account account) {
        return HashUtility.hash(account.getName() + account.getId() + account.getVersion());
    }
}
