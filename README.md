# ENSIT Library Service - Unit Testing & TDD Course Project

## General Context

This project is part of the ENSIT university curriculum for second-year students. Its main objective is to introduce students to the principles and practices of Unit Testing and Test-Driven Development (TDD) in software engineering, using a library management system as a case study.

## Installation Guide

### Prerequisites

Before setting up this project, ensure you have the following installed:

- Java 21
- Apache Maven 3.8.6

### Java 21 Installation

#### Windows

1. Download Java 21 from [Oracle JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) or [OpenJDK](https://openjdk.java.net/install/)
2. Run the installer and follow the on-screen instructions
3. Set environment variables:
   - Set `JAVA_HOME` to the installation directory (e.g., `C:\Program Files\Java\jdk-21`)
   - Add `%JAVA_HOME%\bin` to the `PATH` environment variable
4. Verify installation: Open Command Prompt and run `java -version`

#### Linux (Ubuntu/Debian)

1. Update package list: `sudo apt update`
2. Install OpenJDK 21: `sudo apt install openjdk-21-jdk`
3. Verify installation: `java -version`

#### macOS

1. Install Homebrew if not already installed: `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"`
2. Install OpenJDK 21: `brew install openjdk@21`
3. Add to PATH: `echo 'export PATH="/usr/local/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc` (or ~/.bash_profile for bash)
4. Reload shell: `source ~/.zshrc`
5. Verify installation: `java -version`

### Apache Maven 3.8.6 Installation

#### Windows

1. Download Apache Maven 3.8.6 from [Apache Maven Downloads](https://maven.apache.org/download.cgi)
2. Extract the zip file to a directory (e.g., `C:\apache-maven-3.8.6`)
3. Set environment variables:
   - Set `MAVEN_HOME` to the extraction directory
   - Add `%MAVEN_HOME%\bin` to the `PATH` environment variable
4. Verify installation: Open Command Prompt and run `mvn -version`

#### Linux

1. Download Apache Maven 3.8.6: `wget https://downloads.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz`
2. Extract: `tar -xzf apache-maven-3.8.6-bin.tar.gz`
3. Move to /opt: `sudo mv apache-maven-3.8.6 /opt/`
4. Add to PATH: `echo 'export PATH="/opt/apache-maven-3.8.6/bin:$PATH"' >> ~/.bashrc`
5. Reload shell: `source ~/.bashrc`
6. Verify installation: `mvn -version`

#### macOS

1. Download Apache Maven 3.8.6 from [Apache Maven Downloads](https://maven.apache.org/download.cgi)
2. Extract the tar.gz file: `tar -xzf apache-maven-3.8.6-bin.tar.gz`
3. Move to /usr/local: `sudo mv apache-maven-3.8.6 /usr/local/apache-maven-3.8.6`
4. Add to PATH: `echo 'export PATH="/usr/local/apache-maven-3.8.6/bin:$PATH"' >> ~/.zshrc`
5. Reload shell: `source ~/.zshrc`
6. Verify installation: `mvn -version`

## Why Unit Tests Matter

Unit tests are automated tests written to verify the behavior of small units of code (usually methods or classes). They are essential for:

- **Fearless Refactoring**: With a solid suite of unit tests, developers can change code confidently, knowing that regressions will be caught early.
- **Bug Prevention**: Unit tests catch bugs before code reaches production, reducing maintenance costs and improving reliability.
- **Documentation**: Tests serve as living documentation, showing how code is intended to be used.
- **Faster Development**: Automated tests speed up development by providing instant feedback.

## Test-Driven Development (TDD)

TDD is a software development approach where tests are written before the code itself. The TDD cycle consists of three steps:

1. **Red**: Write a failing test for a new feature.
2. **Green**: Write the minimum code necessary to make the test pass.
3. **Refactor**: Clean up the code, ensuring all tests still pass.

```
+-------------------+
|   Write Test      |
+-------------------+
          |
          v
+-------------------+
|   Run Test (Red)  |
+-------------------+
          |
          v
+-------------------+
|   Write Code      |
+-------------------+
          |
          v
+-------------------+
|   Run Test (Green)|
+-------------------+
          |
          v
+-------------------+
|   Refactor        |
+-------------------+
          |
          v
   (Repeat Cycle)
```

TDD leads to better-designed, more maintainable, and bug-free code.

## Technical Documentation

### Tools & Technologies

- **Java**: Main programming language.
- **Maven**: Build automation and dependency management.
- **JUnit**: Unit testing framework for Java.
- **Mockito**: Mocking framework for unit tests.

### How to Build & Test

- Build the project: `mvn clean install`
- Run tests: `mvn test`

## Project Structure & Application Layers

```
src/
  main/
    java/
      com/tuto/library/
        domain/      # Core entities (Book, Member, Loan, etc.)
        service/     # Business logic (BookService, LoanService, etc.)
        exception/   # Custom exceptions (BookNotFoundException, etc.)
  test/
    java/
      com/tuto/library/
        service/     # Unit tests for services
```

### Layered Architecture

- **Domain Layer**: Contains the main entities and their logic.
- **Service Layer**: Implements business rules and operations.
- **Exception Layer**: Defines custom exceptions for error handling.
- **Test Layer**: Contains unit tests for all major components.

---

This project demonstrates best practices in unit testing and TDD, preparing students for professional software development.
