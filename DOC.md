# Library Service - Technical Documentation

## Overview

The Library Service is a modern RESTful microservice built using **Quarkus**, a Kubernetes-native Java framework optimized for cloud-native applications. This service manages a library's core operations including books, members, and loan transactions.

## Technology Stack

### Core Technologies

#### Quarkus Framework (v3.24.5)
- **Purpose**: Supersonic subatomic Java framework designed for cloud-native development
- **Key Features**:
  - Fast startup time and low memory footprint
  - Live coding with hot reload capabilities
  - Native compilation via GraalVM
  - Built-in container-first philosophy
  - Reactive and imperative programming models

#### Maven
- **Purpose**: Build automation and dependency management
- **Configuration**: `pom.xml` defines project structure, dependencies, and build plugins
- **Java Version**: 21 (latest LTS)

### Web Layer

#### RESTEasy
- **Purpose**: JAX-RS implementation for building RESTful web services
- **Usage**: Handles HTTP request/response processing
- **Features**:
  - Annotation-based endpoint configuration (`@Path`, `@GET`, `@POST`, etc.)
  - Content negotiation
  - Exception mapping

#### RESTEasy JSONB
- **Purpose**: JSON binding for automatic serialization/deserialization
- **Usage**: Converts between Java objects and JSON representations

#### SmallRye OpenAPI
- **Purpose**: OpenAPI/Swagger specification generation
- **Usage**: Automatic API documentation generation

### Persistence Layer

#### Hibernate ORM with Panache
- **Purpose**: Object-Relational Mapping (ORM) with simplified data access patterns
- **Key Components**:
  - **Hibernate ORM**: Industry-standard JPA implementation
  - **Panache**: Quarkus extension that simplifies Hibernate ORM usage
- **Features**:
  - Repository pattern implementation
  - Active Record pattern support
  - Simplified query methods
  - Transaction management

#### JPA (Jakarta Persistence API)
- **Purpose**: Java specification for managing relational data
- **Usage**: Entity mapping and persistence operations
- **Annotations**: `@Entity`, `@Id`, `@Column`, `@GeneratedValue`

#### PostgreSQL Database
- **Version**: 16.3
- **Configuration**:
  - Database name: `library`
  - Username: `libraryuser`
  - Dev Services: Automatic database provisioning in development mode

### Dependency Injection

#### CDI (Contexts and Dependency Injection) with Arc
- **Purpose**: Dependency injection and lifecycle management
- **Implementation**: Arc (Quarkus's CDI implementation)
- **Annotations**:
  - `@ApplicationScoped`: Application-wide singleton beans
  - `@Inject`: Dependency injection
  - `@Transactional`: Transaction demarcation

### Configuration

#### YAML Configuration
- **File**: `application.yml`
- **Purpose**: Centralized application configuration
- **Key Configurations**:
  - Database connection settings
  - Application name
  - HTTP root path (`/api`)

## Project Architecture

### Layered Architecture Pattern

The project follows a clean layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────┐
│         REST Resources              │  ← Presentation Layer
├─────────────────────────────────────┤
│         Services                    │  ← Business Logic Layer
├─────────────────────────────────────┤
│         Repositories                │  ← Data Access Layer
├─────────────────────────────────────┤
│         Domain Entities             │  ← Domain Model
└─────────────────────────────────────┘
```

### Layer Descriptions

#### 1. Domain Layer (`com.tuto.library.domain`)
- **Purpose**: Core business entities and domain logic
- **Components**:
  - `Book`: Book entity with JPA annotations
  - `Member`: Library member entity
  - `Loan`: Loan transaction entity
  - `LoanStatus`: Enumeration for loan states
- **Characteristics**:
  - Pure domain objects with JPA annotations
  - Encapsulates business rules
  - Database-agnostic business logic

#### 2. Repository Layer (`com.tuto.library.repository`)
- **Purpose**: Data access abstraction
- **Pattern**: Repository pattern using Panache
- **Components**:
  - `BookRepository`: Book data operations
  - `MemberRepository`: Member data operations
  - `LoanRepository`: Loan data operations
- **Features**:
  - Extends `PanacheRepository<T>`
  - Provides CRUD operations out-of-the-box
  - Custom query methods when needed

#### 3. Service Layer (`com.tuto.library.service`)
- **Purpose**: Business logic and transaction management
- **Components**:
  - `BookService`: Book management logic
  - `MemberService`: Member management logic
  - `LoanService`: Loan processing and validation
- **Responsibilities**:
  - Transaction boundaries (`@Transactional`)
  - Business rule enforcement
  - Data transformation (Entity ↔ TO)
  - Exception handling

#### 4. Resource Layer (`com.tuto.library.resource`)
- **Purpose**: REST API endpoints
- **Components**:
  - `BookResource`: Book CRUD endpoints
  - `MemberResource`: Member CRUD endpoints
  - `LoanResource`: Loan management endpoints
- **Features**:
  - JAX-RS annotations for HTTP mapping
  - Request validation
  - Response formatting
  - HTTP status code management

#### 5. Transfer Objects (`com.tuto.library.transferobjects`)
- **Purpose**: Data transfer between layers and external clients
- **Components**:
  - `BookTO`: Book data transfer object
  - `MemberTO`: Member data transfer object
  - `LoanTO`: Loan data transfer object
  - `LoanRequestTO`: Loan request payload
- **Benefits**:
  - API contract stability
  - Decoupling from domain model
  - Custom serialization control

#### 6. Exception Layer (`com.tuto.library.exception`)
- **Purpose**: Custom business exceptions
- **Components**:
  - `LibraryException`: Base exception class
  - `BookNotFoundException`: Book not found scenarios
  - `BookNotAvailableException`: Insufficient book copies
  - `MemberNotFoundException`: Member not found scenarios
  - `LoanNotFoundException`: Loan not found scenarios
  - `InvalidLoanOperationException`: Invalid loan operations

## Testing Infrastructure

### Integration Testing Framework

#### Test Dependencies

##### JUnit 5
- **Purpose**: Core testing framework
- **Extensions**: 
  - `@QuarkusTest`: Starts full Quarkus application for testing
  - `QuarkusTestBeforeEachCallback`: Custom test lifecycle callbacks

##### REST Assured (v5.3.2)
- **Purpose**: REST API testing DSL
- **Features**:
  - Fluent API for HTTP requests
  - Response validation
  - JSON/XML parsing
  - Request/Response specification reuse

##### Mockito (v5.7.0)
- **Purpose**: Mocking framework for unit tests
- **Integration**: `quarkus-junit5-mockito` extension
- **Usage**: Service layer isolation testing

##### AssertJ (v3.24.2)
- **Purpose**: Fluent assertion library
- **Benefits**:
  - Readable assertions
  - Rich comparison methods
  - Better error messages

### Integration Test Structure

#### Test Organization
```
src/test/java/
├── com/tuto/library/resource/
│   ├── BookResourceIT.java
│   ├── MemberResourceIT.java
│   └── LoanResourceIT.java
└── testutils/
    └── TestBeforeEachCallback.java
```

#### Integration Test Patterns

##### 1. Test Class Structure
```java
@QuarkusTest
class BookResourceIT {
    @Inject
    BookRepository bookRepository;
    
    @Transactional
    Book createBook(...) {
        // Test data setup
    }
    
    @Test
    void testEndpoint() {
        // REST Assured test
    }
}
```

##### 2. REST Assured Usage Pattern
```java
given()
    .contentType(ContentType.JSON)
    .body(requestObject)
.when()
    .post("/endpoint")
.then()
    .statusCode(expectedStatus)
    .body("field", is(expectedValue));
```

##### 3. Test Lifecycle Management
- **BeforeEach Callback**: Custom test data cleanup/setup via `TestBeforeEachCallback`
- **Transaction Management**: `@Transactional` for test data isolation
- **Database State**: Dev Services provides fresh PostgreSQL instance

### Test Execution

#### Maven Plugins

##### Surefire Plugin
- **Purpose**: Unit test execution
- **Configuration**:
  - Parallel execution (methods)
  - Thread count: 4
  - Pattern: `**/*Test.java`, `**/*IT.java`

##### Failsafe Plugin
- **Purpose**: Integration test execution
- **Goals**:
  - `integration-test`: Run integration tests
  - `verify`: Verify test results
- **Pattern**: `**/*IT.java`

#### Test Commands
```bash
# Run all tests
mvn test

# Run integration tests only
mvn verify

# Run with Quarkus dev mode (includes continuous testing)
mvn quarkus:dev
```

## API Design

### RESTful Endpoints

All endpoints are prefixed with `/api` (configured in `application.yml`).

#### Books API
- `GET /api/books` - List all books
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create new book
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

#### Members API
- `GET /api/members` - List all members
- `GET /api/members/{id}` - Get member by ID
- `POST /api/members` - Register new member
- `PUT /api/members/{id}` - Update member
- `DELETE /api/members/{id}` - Delete member

#### Loans API
- `GET /api/loans` - List all loans
- `GET /api/loans/{id}` - Get loan by ID
- `POST /api/loans` - Create new loan
- `PUT /api/loans/{id}/return` - Return a book
- `GET /api/loans/member/{memberId}` - Get loans by member

### Data Flow

1. **Request Reception**: REST Resource receives HTTP request
2. **Validation**: Resource layer validates request parameters
3. **Business Logic**: Service layer processes business rules
4. **Data Access**: Repository layer handles database operations
5. **Response Formation**: TO objects formatted and returned

## Development Features

### Quarkus Dev Services
- **Automatic Database**: PostgreSQL container started automatically
- **Configuration**: Zero-config database for development
- **Image**: `postgres:16.3`

### Live Coding
- **Hot Reload**: Code changes reflected without restart
- **Command**: `mvn quarkus:dev`
- **Continuous Testing**: Tests run automatically on code changes

### Native Compilation
- **GraalVM Support**: Compile to native executable
- **Benefits**: 
  - Millisecond startup times
  - Reduced memory footprint
  - Container optimization

## Best Practices Implemented

1. **Separation of Concerns**: Clear layer boundaries
2. **Dependency Injection**: Constructor-based injection
3. **Transaction Management**: Service layer transactions
4. **DTO Pattern**: API stability through Transfer Objects
5. **Repository Pattern**: Data access abstraction
6. **Integration Testing**: Comprehensive API testing
7. **Configuration Externalization**: YAML-based configuration
8. **Exception Handling**: Custom domain exceptions
9. **RESTful Design**: Standard HTTP methods and status codes
10. **Database Independence**: JPA/Hibernate abstraction