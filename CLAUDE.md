# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Test
- **Build project**: `mvn clean install`
- **Run tests**: `mvn test`
- **Run specific test class**: `mvn test -Dtest=BookServiceTest`
- **Run specific test method**: `mvn test -Dtest=BookServiceTest#shouldReturnTrue_whenBookIsAvailable`

### Dependencies and Setup
- Java 21 required
- Maven 3.8.6+ required
- No additional lint or format commands configured

## Project Architecture

This is a Java-based library management system designed for teaching Unit Testing and Test-Driven Development (TDD). The project follows a layered architecture pattern:

### Core Package Structure
```
com.tuto.library/
├── domain/         # Core business entities
├── service/        # Business logic layer
├── repository/     # Data access interfaces
└── exception/      # Custom exception hierarchy
```

### Domain Model
The system manages three core entities with clear relationships:
- **Book**: Manages book inventory with available/total copies tracking
- **Member**: Represents library patrons
- **Loan**: Links books to members with status tracking (ACTIVE, RETURNED, OVERDUE)

### Key Business Rules
- Books can only be borrowed if copies are available
- Loans have a 14-day period by default
- System tracks inventory changes during borrow/return operations
- Loan status transitions: ACTIVE → RETURNED or OVERDUE

## Testing Architecture

### Test Framework Stack
- **JUnit 5**: Primary testing framework
- **Mockito**: Mocking framework for dependencies
- **AssertJ**: Fluent assertions library

### Test Structure
Tests are organized in parallel to main code structure:
- Service layer tests focus on business logic validation
- Repository dependencies are mocked using Mockito
- Tests follow TDD principles (Red-Green-Refactor cycle)

### Test Execution Configuration
- Parallel test execution enabled (4 threads)
- Surefire plugin configured for `**/*Test.java` pattern

## Development Notes

### TDD Workflow
This project emphasizes Test-Driven Development. When implementing new features:
1. Write failing tests first (Red phase)
2. Implement minimal code to pass tests (Green phase)
3. Refactor while keeping tests green (Refactor phase)

### Current State
- Domain entities are complete with basic functionality
- Service layer has partial implementation with some commented code
- Test classes exist but contain TODO markers for student exercises
- Repository interfaces are defined but implementations are mocked in tests

### Key Incomplete Areas
- BookService methods for availability checking and inventory management
- Full integration between LoanService and BookService
- Complete test coverage for all service methods

## Student Exercise Context

This codebase contains specific TODOs marked for educational purposes:
- Instructor TODOs: Exception testing demonstrations
- Student TODOs: TDD implementation exercises for BookService functionality

When working on this code, preserve the educational structure and TODO markers unless explicitly asked to complete them.