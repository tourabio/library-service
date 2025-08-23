# ENSIT Library Service - Unit Testing & TDD Course Project

## General Context
This project is part of the ENSIT university curriculum for second-year students. Its main objective is to introduce students to the principles and practices of Unit Testing and Test-Driven Development (TDD) in software engineering, using a library management system as a case study.

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
