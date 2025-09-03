# Library Management System - Angular Frontend

## Project Overview
This is the Angular frontend for a Library Management System that interfaces with an existing Quarkus backend. The system manages books, members, and loans with a focus on creating an immersive, library-themed user experience.

## Backend Service Architecture

### **Technology Stack**
- **Framework**: Quarkus 3.24.5 (Red Hat's Kubernetes-native Java framework)
- **Java Version**: JDK 21
- **Database**: PostgreSQL 16.3 (containerized via Quarkus Dev Services)
- **ORM**: Hibernate ORM with JPA annotations
- **API Style**: JAX-RS RESTful services
- **Serialization**: JSON-B for JSON processing
- **Dependency Injection**: CDI (Contexts and Dependency Injection)

### **Backend Project Structure**
```
src/main/java/com/tuto/library/
├── Main.java                    # Application entry point
├── domain/                      # JPA Entity classes
│   ├── Book.java               # Book entity with JPA annotations
│   ├── Member.java             # Member entity with JPA annotations
│   ├── Loan.java               # Loan entity with relationships
│   └── LoanStatus.java         # Enum for loan statuses
├── repository/                  # Data access layer
│   ├── BookRepository.java     # Book data operations
│   ├── MemberRepository.java   # Member data operations
│   └── LoanRepository.java     # Loan data operations
├── service/                     # Business logic layer
│   ├── BookService.java        # Book business operations
│   ├── MemberService.java      # Member business operations
│   └── LoanService.java        # Loan business operations
├── resource/                    # REST API endpoints
│   ├── BookResource.java       # Book REST endpoints
│   ├── MemberResource.java     # Member REST endpoints
│   └── LoanResource.java       # Loan REST endpoints
├── transferobjects/             # DTOs for API communication
│   ├── BookTO.java             # Book transfer object (record)
│   ├── MemberTO.java           # Member transfer object (record)
│   ├── LoanTO.java             # Loan transfer object (record)
│   └── LoanRequestTO.java      # Loan request transfer object
└── exception/                   # Custom exception classes
    ├── LibraryException.java   # Base library exception
    ├── BookNotFoundException.java
    ├── MemberNotFoundException.java
    ├── LoanNotFoundException.java
    ├── BookNotAvailableException.java
    └── InvalidLoanOperationException.java
```

### **Database Schema & Entities**

#### **Book Entity**
```java
@Entity
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(name = "total_copies", nullable = false)
    private int totalCopies;
    
    @Column(name = "available_copies", nullable = false)
    private int availableCopies;
}
```

#### **Member Entity**
```java
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
}
```

#### **Loan Entity**
```java
@Entity
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status; // ACTIVE, RETURNED, OVERDUE
}
```

### **REST API Endpoints**

#### **Base URL**: `/api` (configured in application.yml)

#### **Book Management APIs**
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create new book
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

#### **Member Management APIs**
- `GET /api/members` - Get all members
- `GET /api/members/{id}` - Get member by ID
- `POST /api/members` - Create new member
- `PUT /api/members/{id}` - Update member
- `DELETE /api/members/{id}` - Delete member

#### **Loan Management APIs**
- `GET /api/loans` - Get all loans
- `GET /api/loans/{id}` - Get loan by ID
- `POST /api/loans` - Create new loan (checkout book)
- `PUT /api/loans/{id}/return` - Return book (complete loan)
- `GET /api/loans/overdue` - Get overdue loans
- `GET /api/loans/member/{memberId}` - Get loans by member
- `GET /api/loans/book/{bookId}` - Get loans by book

### **Transfer Objects (DTOs)**
Using Java 17 Records for immutable DTOs:

```java
// Simplified, immutable transfer objects
public record BookTO(String title, String author, int totalCopies, int availableCopies) {}
public record MemberTO(String name, String email) {}
public record LoanTO(Long id, Long bookId, Long memberId, LocalDate loanDate, 
                    LocalDate dueDate, LocalDate returnDate, LoanStatus status) {}
public record LoanRequestTO(Long bookId, Long memberId) {}
```

### **Business Logic & Rules**
1. **Book Availability**: Available copies decremented on loan, incremented on return
2. **Loan Duration**: Default 14 days from loan date
3. **Overdue Detection**: Loans past due date with ACTIVE status
4. **Member Validation**: Email must be unique across all members
5. **Book Validation**: Cannot loan book with 0 available copies
6. **Return Logic**: Sets return date and updates book availability

### **Database Configuration**
```yaml
# application.yml
quarkus:
  datasource:
    db-kind: postgresql
    devservices:
      image-name: postgres:16.3
      db-name: library
    username: libraryuser
    password: librarypass2025
  
  application:
    name: Library Service
  
  http:
    root-path: /api
```

### **Error Handling Strategy**
- **Custom Exceptions**: Domain-specific exceptions for business logic errors
- **Global Exception Handlers**: Centralized error response formatting
- **HTTP Status Codes**: Proper REST status codes (404, 400, 500, etc.)
- **Error Response Format**: Consistent JSON error structure

### **Testing Infrastructure**
- **Integration Tests**: REST resource integration tests using RestAssured
- **Test Database**: Separate test database configuration
- **Test Utilities**: Custom test lifecycle management
- **Coverage**: JUnit 5 with comprehensive test coverage

### **Development & Deployment**
- **Dev Mode**: Quarkus Live Coding for instant feedback
- **Build Tool**: Maven with Quarkus extensions
- **Container Ready**: Native compilation and containerization support
- **Hot Reload**: Automatic recompilation during development

### **Frontend-Backend Integration**

#### **API Communication Patterns**
- **HTTP Client**: Angular's HttpClient for all API communication
- **Base URL**: `http://localhost:8080/api` (development), configurable per environment
- **Content Type**: `application/json` for all requests/responses
- **Error Handling**: Consistent error response format across all endpoints
- **Loading States**: UI loading indicators for all API calls

#### **Data Transformation**
```typescript
// Frontend TypeScript interfaces must match backend DTOs
interface Book {
  readonly id: string;           // Backend: Long -> Frontend: string
  readonly title: string;        // Direct mapping
  readonly author: string;       // Direct mapping  
  readonly totalCopies: number;  // Direct mapping
  readonly availableCopies: number; // Direct mapping
  readonly createdAt: Date;      // Additional frontend fields
  readonly updatedAt: Date;      // Additional frontend fields
}

// API Request/Response handling
createBook(request: CreateBookRequest): Observable<Book> {
  return this.http.post<BookTO>(`${this.apiUrl}/books`, request)
    .pipe(
      map(bookTO => this.mapToFrontendBook(bookTO)),
      catchError(this.handleError)
    );
}
```

#### **Service Layer Integration**
- **BookService**: Maps backend BookTO to frontend Book interface
- **MemberService**: Maps backend MemberTO to frontend Member interface  
- **LoanService**: Maps backend LoanTO to frontend Loan interface
- **Error Mapping**: Transform backend exceptions to user-friendly messages
- **Date Handling**: Convert LocalDate strings to Date objects

#### **State Synchronization**
- **Optimistic Updates**: Update UI immediately, rollback on API failure
- **Real-time Updates**: WebSocket/SSE for live data updates (future enhancement)
- **Cache Management**: Service-level caching for frequently accessed data
- **Conflict Resolution**: Handle concurrent updates gracefully

## Design Philosophy & Theme

### **Brown Library Theme**
Create a warm, inviting library atmosphere using rich brown tones:
- **Primary Colors**: 
  - Deep Brown: `#3E2723` (dark wood)
  - Medium Brown: `#5D4037` (leather bindings)
  - Light Brown: `#8D6E63` (aged paper)
  - Warm Beige: `#EFEBE9` (parchment)
  - Accent Gold: `#FFB300` (vintage brass)

### **Design Principles**
- **Warm & Inviting**: Use soft shadows, rounded corners, and warm lighting effects
- **Library Aesthetics**: Incorporate book spines, reading nooks, wooden textures
- **Accessibility First**: Ensure WCAG 2.1 AA compliance with proper contrast ratios
- **Responsive Design**: Mobile-first approach with elegant desktop enhancements
- **Creative Elements**: Subtle animations, hover effects, and micro-interactions

### **UI Components Style Guide**
- **Cards**: Book-like appearance with subtle shadows and rounded corners
- **Buttons**: Leather-texture inspired with gold accent hover states
- **Forms**: Parchment-style backgrounds with elegant focus states
- **Navigation**: Wooden shelf aesthetic with book spine tabs
- **Icons**: Line icons with warm brown colors, library-themed where possible
- **Typography**: Serif fonts for headers (library feel), sans-serif for body text

## Architecture & Folder Structure

### **Feature-Based Architecture**
```
src/
├── app/
│   ├── core/                     # Singleton services, guards, interceptors
│   │   ├── services/            # HTTP client services, auth service
│   │   ├── guards/              # Route guards
│   │   ├── interceptors/        # HTTP interceptors
│   │   └── models/              # TypeScript interfaces/types
│   ├── shared/                   # Reusable components, pipes, directives
│   │   ├── components/          # Common UI components
│   │   ├── pipes/               # Custom pipes
│   │   ├── directives/          # Custom directives
│   │   └── utils/               # Utility functions
│   ├── features/                # Feature modules
│   │   ├── books/               # Book management feature
│   │   ├── members/             # Member management feature
│   │   ├── loans/               # Loan management feature
│   │   └── dashboard/           # Dashboard/home feature
│   ├── layout/                  # Layout components
│   │   ├── header/
│   │   ├── sidebar/
│   │   └── footer/
│   └── theme/                   # Theme-related files
│       ├── variables.scss       # SCSS variables for brown theme
│       ├── mixins.scss         # Reusable SCSS mixins
│       └── components.scss     # Global component styles
```

### **Smart/Dumb Component Pattern**
- **Smart Components**: Handle business logic, API calls, state management
- **Dumb Components**: Pure presentation, receive data via @Input, emit via @Output
- **Services**: Handle all HTTP communication with backend
- **Models**: Strong typing for all API responses and requests

## Development Standards

### **TypeScript Guidelines**
- **Strict Mode**: Enable all strict TypeScript compiler options
- **No `any` Type**: Use proper typing, create interfaces for all API responses
- **Explicit Return Types**: All functions must have explicit return type annotations
- **Interface Naming**: Prefix with 'I' (e.g., `IBook`, `IMember`, `ILoan`)
- **Enum Usage**: Use enums for constants and status values

### **Angular Best Practices**
- **OnPush Change Detection**: Use for all components where possible
- **Reactive Forms**: Use reactive forms exclusively, no template-driven forms
- **RxJS Patterns**: 
  - Use observables for all async operations
  - Implement proper error handling with `catchError`
  - Use `takeUntilDestroyed()` for subscription management
  - Prefer `switchMap`, `mergeMap`, `concatMap` over nested subscriptions
- **Lazy Loading**: All feature modules must be lazily loaded
- **Track By Functions**: Always use trackBy for *ngFor with dynamic data

### **Code Quality Standards**
- **Single Responsibility**: Each class/component has one clear purpose
- **DRY Principle**: No code duplication, extract reusable utilities
- **SOLID Principles**: Apply SOLID principles to service design
- **Pure Functions**: Prefer pure functions where possible
- **Immutability**: Use immutable update patterns for state changes

### **File Naming Conventions**
- **Components**: `book-list.component.ts`
- **Services**: `book.service.ts`
- **Models**: `book.model.ts`
- **Guards**: `auth.guard.ts`
- **Pipes**: `format-date.pipe.ts`
- **Directives**: `highlight.directive.ts`

## UI/UX Requirements

### **Core Features UI**
1. **Dashboard**: Library overview with stats, recent activities
2. **Book Management**: 
   - Book catalog with grid/list view toggle
   - Search and filtering capabilities
   - Book details modal with availability status
   - Add/edit book forms with image upload
3. **Member Management**:
   - Member directory with search
   - Member profile pages with loan history
   - Member registration/edit forms
4. **Loan Management**:
   - Active loans dashboard
   - Loan checkout process
   - Return book functionality
   - Overdue loans highlighting

### **Navigation & Layout**
- **Primary Navigation**: Sidebar with library shelf design
- **Breadcrumbs**: Show current location in library hierarchy
- **Search**: Global search bar with type-ahead suggestions
- **User Menu**: Profile, settings, logout options

### **Responsive Breakpoints**
- **Mobile**: < 768px (stack vertically, hamburger menu)
- **Tablet**: 768px - 1024px (optimize for touch)
- **Desktop**: > 1024px (full sidebar, multi-column layouts)

## Testing Strategy

### **Jest Configuration**
- **Unit Tests**: All components, services, pipes, and utilities
- **Coverage Requirement**: Minimum 80% code coverage
- **Test Structure**: Arrange-Act-Assert pattern
- **Mocking**: Mock all external dependencies and HTTP calls

### **Testing Guidelines**
- **Component Tests**: Test user interactions, input/output behavior
- **Service Tests**: Test all public methods, error scenarios
- **Integration Tests**: Test feature workflows end-to-end
- **Snapshot Tests**: For stable UI components (sparingly)

### **Test File Conventions**
- **Unit Tests**: `*.component.spec.ts`, `*.service.spec.ts`
- **Integration Tests**: `*.integration.spec.ts`
- **Test Utilities**: `src/testing/` directory for test helpers

## Linting & Code Style

### **ESLint Configuration**
- **Base**: `@angular-eslint/recommended`
- **TypeScript**: `@typescript-eslint/recommended`
- **Custom Rules**:
  - Enforce explicit return types
  - Prohibit `any` type usage
  - Require JSDoc comments for public methods
  - Enforce consistent naming conventions

### **Prettier Integration**
- **Print Width**: 120 characters
- **Tab Width**: 2 spaces
- **Semicolons**: Always required
- **Single Quotes**: Preferred for strings
- **Trailing Commas**: ES5 compatible

### **SCSS Linting**
- **Stylelint**: Enforce SCSS best practices
- **BEM Methodology**: Use BEM naming for component-specific styles
- **Nesting Depth**: Maximum 3 levels of nesting

## State Management

### **Service-Based State (Preferred)**
- Use services with BehaviorSubjects for simple state
- Implement proper loading states and error handling
- Follow reactive patterns with observables

### **NgRx (If Needed)**
- Only introduce NgRx if state complexity warrants it
- Follow NgRx style guide and best practices
- Implement proper error handling and loading states

## Performance Guidelines

### **Optimization Strategies**
- **OnPush Change Detection**: Use throughout the application
- **Lazy Loading**: All feature modules
- **Image Optimization**: Properly sized images with lazy loading
- **Bundle Analysis**: Regular bundle size monitoring
- **Caching**: Implement HTTP caching strategies

### **Performance Budgets**
- **Initial Bundle**: < 500KB
- **Feature Bundles**: < 200KB each
- **First Contentful Paint**: < 2 seconds
- **Time to Interactive**: < 5 seconds

## Development Workflow

### **Branch Strategy**
- **Main Branch**: Production-ready code
- **Develop Branch**: Integration branch
- **Feature Branches**: `feature/book-management`, `feature/member-dashboard`
- **Bug Fix Branches**: `bugfix/loan-calculation-error`

### **Commit Messages**
Follow conventional commit format:
- `feat: add book search functionality`
- `fix: resolve loan due date calculation`
- `style: update brown theme color palette`
- `refactor: extract book service utilities`
- `test: add comprehensive loan service tests`

### **Pre-commit Hooks**
- **Linting**: ESLint + Stylelint must pass
- **Testing**: All tests must pass
- **Type Checking**: No TypeScript errors
- **Formatting**: Prettier auto-formatting

## Deployment & Build

### **Environment Configuration**
- **Development**: Local development with mock data
- **Staging**: Connected to staging Quarkus backend
- **Production**: Connected to production Quarkus backend

### **Build Optimization**
- **Production Builds**: AOT compilation, tree shaking, minification
- **Source Maps**: Generate for staging, exclude from production
- **Service Worker**: Implement for offline capability

## Documentation

### **Code Documentation**
- **JSDoc**: All public methods and complex logic
- **README**: Setup, development, and deployment instructions
- **Architecture Docs**: High-level system design documentation
- **API Integration**: Document all backend API integrations

### **User Documentation**
- **User Guide**: How to use the library management system
- **Admin Guide**: Administrative features and configurations

## Accessibility & Internationalization

### **Accessibility Requirements**
- **WCAG 2.1 AA**: Full compliance required
- **Screen Readers**: Proper ARIA labels and roles
- **Keyboard Navigation**: Full keyboard accessibility
- **Color Contrast**: Meet minimum contrast ratios with brown theme

### **Internationalization (Future)**
- **Angular i18n**: Prepare for multi-language support
- **Date/Number Formatting**: Locale-aware formatting
- **Text Extraction**: All user-facing text in translation files

## Error Handling & Logging

### **Error Strategy**
- **Global Error Handler**: Catch and log all unhandled errors
- **User-Friendly Messages**: Meaningful error messages for users
- **Retry Logic**: Implement retry for transient failures
- **Fallback UI**: Graceful degradation for API failures

### **Logging**
- **Development**: Console logging with debug information
- **Production**: Structured logging to external service (if configured)
- **User Actions**: Track important user interactions

---

## Quick Start Commands

### **Development Setup**
```bash
npm install
npm start                    # Start development server
npm run test                 # Run Jest tests
npm run test:watch          # Run tests in watch mode
npm run lint                # Run ESLint
npm run lint:fix            # Fix ESLint issues
npm run e2e                 # Run e2e tests (if configured)
```

### **Quality Assurance**
```bash
npm run build              # Production build
npm run analyze            # Bundle analysis
npm run test:coverage      # Test coverage report
npm audit                  # Security audit
```

## Restrictions & Guidelines

### **Prohibited Practices**
- No direct DOM manipulation (use Angular patterns)
- No jQuery or other external DOM libraries
- No inline styles (use component stylesheets)
- No `any` types without explicit justification
- No subscription without proper cleanup
- No hardcoded strings (use constants/enums)

### **Required Practices**
- All components must have proper TypeScript typing
- All HTTP calls must have error handling
- All forms must have validation
- All user actions must have loading states
- All public methods must have JSDoc comments
- All features must have corresponding tests

This frontend will create a beautiful, functional library management interface that complements your Quarkus backend while maintaining high code quality and user experience standards.