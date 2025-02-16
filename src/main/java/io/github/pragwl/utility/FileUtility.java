package io.github.pragwl.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/** Utility class for file operations. */
@Slf4j
public final class FileUtility {

    private FileUtility() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves a list of file names from the given directory.
     *
     * @param directoryName The name of the directory.
     * @return A list of file names, or an empty list if the directory does not exist or is empty.
     */
    public static List<String> getFilesListFromDirectory(String directoryName) {
        if (directoryName == null || directoryName.isEmpty()) {
            log.warn("Directory name is null or empty.");
            return Collections.emptyList();
        }

        File directory = new File(directoryName);
        if (!directory.exists()) {
            log.warn("Directory does not exist: {}", directoryName);
            return Collections.emptyList();
        }

        if (!directory.isDirectory()) {
            log.warn("Not a directory: {}", directoryName);
            return Collections.emptyList();
        }

        File[] files = directory.listFiles();
        if (files == null) {
            log.warn("Error listing files in directory: {}", directoryName);
            return Collections.emptyList();
        }

        return Arrays.stream(files)
                .filter(File::isFile)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * Moves a file from the source folder to the destination folder.
     *
     * @param sourceFolder The source folder.
     * @param destinationFolder The destination folder.
     * @param fileName The name of the file to move.
     * @throws RuntimeException if the file cannot be moved.
     */
    public static void moveFile(String sourceFolder, String destinationFolder, String fileName) {
        Path sourcePath = Paths.get(sourceFolder, fileName);
        Path destinationPath = Paths.get(destinationFolder, fileName);

        try {
            // Create destination folder if it doesn't exist
            if (!Files.exists(destinationPath.getParent())) {
                Files.createDirectories(destinationPath.getParent());
            }

            // Move the file
            Files.move(sourcePath, destinationPath);
            log.info("Moved file from {} to {}", sourcePath, destinationPath);

        } catch (IOException e) {
            log.error("Failed to move file from {} to {}: {}", sourcePath, destinationPath, e.getMessage());
            throw new RuntimeException("Failed to move file: " + e.getMessage(), e); // Include the exception
        }
    }

    /**
     * Deletes a file.
     *
     * @param filePath The path to the file to delete.
     * @return `true` if the file was deleted successfully, `false` otherwise.
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            log.warn("File does not exist: {}", filePath);
            return false;
        }

        boolean deleted = file.delete();
        if (deleted) {
            log.info("Deleted file: {}", filePath);
        } else {
            log.error("Failed to delete file: {}", filePath);
        }
        return deleted;
    }
}