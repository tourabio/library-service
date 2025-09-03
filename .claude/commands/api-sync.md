---
allowed-tools: Read(src/app/core/models/**), Edit(src/app/core/models/**), Read(src/environments/**), Read(src/app/core/services/**)
description: Synchronize frontend models with Quarkus backend API
---
Synchronize Angular frontend models with Quarkus backend API:

BACKEND CONTEXT:
- Framework: Quarkus 3.24.5 with Java 21
- Database: PostgreSQL 16.3 with JPA/Hibernate
- API Base URL: /api (configured in backend)
- Transfer Objects: Java Records (immutable DTOs)
- Status Enums: LoanStatus (ACTIVE, RETURNED, OVERDUE)

BACKEND ENTITIES & ENDPOINTS:
Book Entity (domain/Book.java):
- id: Long (auto-generated)
- title: String (required)
- author: String (required) 
- totalCopies: int (required)
- availableCopies: int (required)
- REST: GET/POST/PUT/DELETE /api/books, GET /api/books/{id}

Member Entity (domain/Member.java):
- id: Long (auto-generated)
- name: String (required)
- email: String (unique, required)
- REST: GET/POST/PUT/DELETE /api/members, GET /api/members/{id}

Loan Entity (domain/Loan.java):
- id: Long (auto-generated)
- book: Book (ManyToOne relationship)
- member: Member (ManyToOne relationship)  
- loanDate: LocalDate (required)
- dueDate: LocalDate (required, default +14 days)
- returnDate: LocalDate (optional)
- status: LoanStatus enum (ACTIVE, RETURNED, OVERDUE)
- REST: GET/POST /api/loans, PUT /api/loans/{id}/return, GET /api/loans/overdue

TRANSFER OBJECTS (DTOs):
- BookTO: record(title, author, totalCopies, availableCopies)
- MemberTO: record(name, email)
- LoanTO: record(BookTO book, MemberTO member, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate, LoanStatus status)
- LoanRequestTO: record(Long bookId, Long memberId)

SYNCHRONIZATION REQUIREMENTS:
- Frontend interfaces must match backend DTOs exactly
- Use number type for Long/int backend fields
- Handle LocalDate as Date objects (conversion required)
- LoanStatus enum must match: ACTIVE | RETURNED | OVERDUE
- API services must use correct endpoint paths (/api/*)
- Remove frontend-only interfaces not supported by backend
- Entity relationships handled via nested TOs, not IDs

VALIDATION TASKS:
- Verify Book/BookTO interface alignment
- Verify Member/MemberTO interface alignment  
- Verify Loan/LoanTO interface alignment
- Update LoanStatus enum (remove unsupported statuses)
- Fix API service endpoint URLs
- Remove unsupported search/pagination interfaces
- Update service method signatures for number IDs
- Align error handling with backend exceptions