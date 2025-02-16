package io.github.pragwl.utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ConsolePrinter {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String INDEX_COLUMN_HEADER = "No"; // Renamed for consistency
    private static final String NULL_VALUE_STRING = "null";

    private ConsolePrinter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Prints a table of objects to the console with dynamic column widths.
     *
     * @param objects The list of objects to print.
     * @param ignoredFields A list of field names to ignore.
     * @param <T> The type of the objects in the list.
     */
    public static <T> void printTable(List<T> objects, List<String> ignoredFields) {
        if (objects == null || objects.isEmpty()) {
            printWarningMessage("List is empty.");
            return;
        }

        Class<?> clazz = objects.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();

        // Calculate maximum column widths
        Map<String, Integer> columnWidths = calculateColumnWidths(objects, fields, ignoredFields);

        // Print table header
        printTableHeader(fields, columnWidths, ignoredFields);

        // Print table content
        printTableContent(objects, fields, columnWidths, ignoredFields);

        SCANNER.nextLine(); // Wait for user to press Enter before continuing
    }

    private static <T> Map<String, Integer> calculateColumnWidths(
            List<T> objects, Field[] fields, List<String> ignoredFields) {
        Map<String, Integer> columnWidths = new HashMap<>();
        columnWidths.put(INDEX_COLUMN_HEADER, INDEX_COLUMN_HEADER.length());

        for (Field field : fields) {
            if (!ignoredFields.contains(field.getName())) {
                columnWidths.put(field.getName(), field.getName().length());
            }
        }

        for (Object obj : objects) {
            for (Field field : fields) {
                if (!ignoredFields.contains(field.getName())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (value != null) {
                            columnWidths.put(
                                    field.getName(),
                                    Math.max(columnWidths.get(field.getName()), value.toString().length()));
                        } else {
                            columnWidths.put(
                                    field.getName(),
                                    Math.max(columnWidths.get(field.getName()), NULL_VALUE_STRING.length())); // account for "null" string length
                        }
                    } catch (IllegalAccessException e) {
                        log.error("Error accessing field: {}", field.getName(), e); // Log the exception with the field name
                    }
                }
            }
        }
        return columnWidths;
    }

    private static void printTableHeader(
            Field[] fields, Map<String, Integer> columnWidths, List<String> ignoredFields) {
        System.out.print(
                padRight(INDEX_COLUMN_HEADER, columnWidths.get(INDEX_COLUMN_HEADER)) + " | "); // Use constant
        for (Field field : fields) {
            if (!ignoredFields.contains(field.getName())) {
                System.out.print(padRight(field.getName(), columnWidths.get(field.getName())) + " | ");
            }
        }
        System.out.println();
    }

    private static <T> void printTableContent(
            List<T> objects, Field[] fields, Map<String, Integer> columnWidths, List<String> ignoredFields) {
        int rowCount = 1;
        for (Object obj : objects) {
            System.out.print(
                    padRight(
                            String.valueOf(rowCount), columnWidths.get(INDEX_COLUMN_HEADER))
                            + " | "); // consistent column
            for (Field field : fields) {
                if (!ignoredFields.contains(field.getName())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        String stringValue = (value != null) ? value.toString() : NULL_VALUE_STRING; // use String
                        System.out.print(padRight(stringValue, columnWidths.get(field.getName())) + " | ");
                    } catch (IllegalAccessException e) {
                        log.error("Error accessing field: {}", field.getName(), e); // Log the exception with the field name
                        System.out.print(padRight("ERROR", columnWidths.get(field.getName())) + " | "); // display "ERROR" and account for padding
                    }
                }
            }
            System.out.println();
            rowCount++;
        }
    }

    /**
     * Prints a warning message to the console.
     *
     * @param message The warning message to print.
     */
    public static void printWarningMessage(String message) {
        System.out.println(message.toUpperCase());
        SCANNER.nextLine();
    }

    /**
     * Pads a string with spaces to the right until it reaches the specified length.
     *
     * @param text The string to pad.
     * @param length The desired length of the padded string.
     * @return The padded string.
     */
    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }
}