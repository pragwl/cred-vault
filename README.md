# cred-vault

A simple credential management application.

## License

This project is licensed under the [MIT License](LICENSE) - see the `LICENSE` file for details. (Create a file named
`LICENSE` in the root of your project if you want to use the MIT License, and put the license text in it.)

## Getting Started

1.  **Prerequisites:**

    *   Java Development Kit (JDK) 17 or higher.
    *   Maven or Gradle (optional, for dependency management).

2.  **Project Structure:**

    The following directories need to exist in your project:

    ```
    cred-vault/
    ├── src/main/java/  (Java source code)
    ├── config/         (Configuration files)
    ├── accounts/       (Where active accounts are stored as encrypted - will be created automatically)
    ├── archived/      (Where archived accounts are stored as encrypted- will be created automatically)
    └── build.gradle    (Gradle project file, optional)
    ```

3.  **Configuration Files:**

    The application requires a configuration file containing an encryption key.

    *   **`config/encflekey.txt`:** This file must contain a 256-bit AES encryption key, Base64 encoded.

4.  **Dependencies:**

    The application uses the following dependencies:

    *   Lombok
    *   SLF4J API and a logging implementation (e.g., Logback)
    *   Apache Commons Lang

5.  **Running the Application:**

    1.  Compile the Java source code.
    2.  Run the `Application` class (the main class).
        *   Using Gradle: `gradle run`

## Creating an Executable JAR File (Distribution)

To package the application into a self-contained executable JAR file for easy distribution, use the following:

**Using Gradle:**

1.  **Add the `shadowJar` plugin to your `build.gradle` file:**

    ```groovy
    plugins {
        id 'com.github.johnrengelman.shadow' version '8.1.1'
        id 'java'
    }

    jar {
        manifest {
            attributes 'Main-Class': 'io.github.pragwl.Application'
        }
    }

    shadowJar {
       archiveBaseName.set("cred-vault")
       archiveClassifier.set("")
       archiveVersion.set("")
    }
    ```

    *   Make sure to replace `"io.github.pragwl.Application"` with the fully qualified name of your main class.
    *   The `shadowJar` block allows you to customize the name of the resulting JAR file.

2.  **Run the `shadowJar` task:**

    ```bash
    gradle shadowJar
    ```

    This will create an executable JAR file in the `build/libs` directory. The file will be named `cred-vault.jar` (or whatever you set in `archiveBaseName`).

3.  **Run the Executable JAR:**

    ```bash
    java -jar build/libs/cred-vault.jar
    ```