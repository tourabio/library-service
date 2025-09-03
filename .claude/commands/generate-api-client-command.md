---
allowed-tools: Bash(npm install:*), Bash(npx:*), Edit(src/**), Read(*.json), Edit(*.json), Bash(curl:*)
argument-hint: [swagger-url-or-file]
description: Generate TypeScript API client from Quarkus backend Swagger specification
---

Generate TypeScript API client from your Quarkus backend OpenAPI specification:

**OpenAPI Source**: ${ARGUMENTS:-http://localhost:8080/q/openapi}

**Backend Context**: Quarkus 3.24.5 with JAX-RS REST endpoints at /api base path

## API CLIENT GENERATION

### **1. Install OpenAPI Generator**
```bash
npm install --save-dev @openapitools/openapi-generator-cli
npm install --save-dev @angular-devkit/build-angular
```

### **2. Configure OpenAPI Generator**
Create `openapitools.json`:
```json
{
  "$schema": "./node_modules/@openapitools/openapi-generator-cli/config.schema.json",
  "spaces": 2,
  "generator-cli": {
    "version": "7.0.1"
  }
}
```

### **3. Generate API Client**
```bash
# Download swagger spec (if URL provided)
npx openapi-generator-cli generate \
  -i ${ARGUMENTS:-http://localhost:8080/q/openapi} \
  -g typescript-angular \
  -o src/app/core/api \
  --additional-properties=ngVersion=18,npmName=library-api-client,supportsES6=true,withInterfaces=true,useSingleRequestParameter=true
```

### **4. Update Generated Client Configuration**
Create `src/app/core/api/api-configuration.ts`:
```typescript
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Global configuration for API client
 */
@Injectable({
  providedIn: 'root',
})
export class ApiConfiguration {
  rootUrl: string = environment.apiUrl;
}
```

### **5. Create API Service Wrapper**
Create `src/app/core/services/api.service.ts`:
```typescript
import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

// Import generated services
import { BookService as GeneratedBookService } from '../api/services/book.service';
import { MemberService as GeneratedMemberService } from '../api/services/member.service';
import { LoanService as GeneratedLoanService } from '../api/services/loan.service';

// Import generated models
import { Book, Member, Loan, CreateBookRequest, CreateMemberRequest, CreateLoanRequest } from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(
    private bookService: GeneratedBookService,
    private memberService: GeneratedMemberService,
    private loanService: GeneratedLoanService
  ) {}

  // Book operations
  getAllBooks(): Observable<Book[]> {
    return this.bookService.getAllBooks().pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getBookById(id: string): Observable<Book> {
    return this.bookService.getBookById({ id }).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  createBook(book: CreateBookRequest): Observable<Book> {
    return this.bookService.createBook({ body: book }).pipe(
      catchError(this.handleError)
    );
  }

  updateBook(id: string, book: Partial<Book>): Observable<Book> {
    return this.bookService.updateBook({ id, body: book }).pipe(
      catchError(this.handleError)
    );
  }

  deleteBook(id: string): Observable<void> {
    return this.bookService.deleteBook({ id }).pipe(
      catchError(this.handleError)
    );
  }

  // Member operations
  getAllMembers(): Observable<Member[]> {
    return this.memberService.getAllMembers().pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getMemberById(id: string): Observable<Member> {
    return this.memberService.getMemberById({ id }).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  createMember(member: CreateMemberRequest): Observable<Member> {
    return this.memberService.createMember({ body: member }).pipe(
      catchError(this.handleError)
    );
  }

  updateMember(id: string, member: Partial<Member>): Observable<Member> {
    return this.memberService.updateMember({ id, body: member }).pipe(
      catchError(this.handleError)
    );
  }

  deleteMember(id: string): Observable<void> {
    return this.memberService.deleteMember({ id }).pipe(
      catchError(this.handleError)
    );
  }

  // Loan operations
  getAllLoans(): Observable<Loan[]> {
    return this.loanService.getAllLoans().pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getLoanById(id: string): Observable<Loan> {
    return this.loanService.getLoanById({ id }).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  createLoan(loan: CreateLoanRequest): Observable<Loan> {
    return this.loanService.createLoan({ body: loan }).pipe(
      catchError(this.handleError)
    );
  }

  returnLoan(id: string): Observable<Loan> {
    return this.loanService.returnLoan({ id }).pipe(
      catchError(this.handleError)
    );
  }

  getOverdueLoans(): Observable<Loan[]> {
    return this.loanService.getOverdueLoans().pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getLoansByMemberId(memberId: string): Observable<Loan[]> {
    return this.loanService.getLoansByMember({ memberId }).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  // Error handling
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error
      switch (error.status) {
        case 400:
          errorMessage = 'Bad Request: Invalid data provided';
          break;
        case 401:
          errorMessage = 'Unauthorized: Please log in';
          break;
        case 403:
          errorMessage = 'Forbidden: Insufficient permissions';
          break;
        case 404:
          errorMessage = 'Not Found: Resource does not exist';
          break;
        case 409:
          errorMessage = 'Conflict: Resource already exists or operation not allowed';
          break;
        case 500:
          errorMessage = 'Server Error: Please try again later';
          break;
        default:
          errorMessage = `Server Error: ${error.status} - ${error.message}`;
      }
    }

    console.error('API Error:', error);
    return throwError(() => new Error(errorMessage));
  }
}
```

### **6. Create Type Definitions Export**
Create `src/app/core/models/api.types.ts`:
```typescript
// Re-export all generated API types for consistent imports
export * from '../api/models';

// Extend generated types if needed
import { Book as GeneratedBook, Member as GeneratedMember, Loan as GeneratedLoan } from '../api/models';

// Add any custom extensions to generated types
export interface BookWithAvailability extends GeneratedBook {
  isAvailable: boolean;
  availabilityStatus: 'available' | 'limited' | 'unavailable';
}

export interface MemberWithStats extends GeneratedMember {
  activeLoansCount: number;
  overdueLoansCount: number;
  totalBooksLoaned: number;
}

export interface LoanWithDetails extends GeneratedLoan {
  bookTitle: string;
  bookAuthor: string;
  memberName: string;
  isOverdue: boolean;
  daysUntilDue: number;
}

// Custom request/response types
export interface DashboardStats {
  totalBooks: number;
  totalMembers: number;
  activeLoans: number;
  overdueLoans: number;
  availableBooks: number;
}

export interface SearchFilters {
  query?: string;
  author?: string;
  availability?: 'all' | 'available' | 'unavailable';
  sortBy?: 'title' | 'author' | 'availability';
  sortOrder?: 'asc' | 'desc';
}

export interface PaginationParams {
  page: number;
  size: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
```

### **7. Update Core Module**
Update `src/app/core/core.module.ts`:
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

// Import generated API module
import { ApiModule } from './api/api.module';
import { ApiConfiguration } from './api/api-configuration';

// Import services
import { ApiService } from './services/api.service';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HttpClientModule,
    ApiModule.forRoot(() => new ApiConfiguration())
  ],
  providers: [
    ApiService,
    // Add other core services here
  ]
})
export class CoreModule {}
```

### **8. Update Environment Configuration**
Ensure `src/environments/environment.ts` has correct API URL:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api', // Quarkus backend uses /api base path
  version: '1.0.0'
};
```

### **9. Add Package.json Scripts**
Add to `package.json` scripts:
```json
{
  "scripts": {
    "api:generate": "npx openapi-generator-cli generate -i http://localhost:8080/q/openapi -g typescript-angular -o src/app/core/api --additional-properties=ngVersion=18,npmName=library-api-client,supportsES6=true,withInterfaces=true,useSingleRequestParameter=true",
    "api:generate:local": "npx openapi-generator-cli generate -i ./openapi.json -g typescript-angular -o src/app/core/api --additional-properties=ngVersion=18,npmName=library-api-client,supportsES6=true,withInterfaces=true,useSingleRequestParameter=true"
  }
}
```

### **10. Create Service Examples**
Create `src/app/features/books/services/book-facade.service.ts`:
```typescript
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators';

import { ApiService } from '../../../core/services/api.service';
import { Book, BookWithAvailability, CreateBookRequest, SearchFilters } from '../../../core/models/api.types';

@Injectable({
  providedIn: 'root'
})
export class BookFacadeService {
  private booksSubject = new BehaviorSubject<Book[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private searchFiltersSubject = new BehaviorSubject<SearchFilters>({});

  public books$ = this.booksSubject.asObservable();
  public loading$ = this.loadingSubject.asObservable();
  public searchFilters$ = this.searchFiltersSubject.asObservable();

  // Computed observables
  public filteredBooks$ = combineLatest([
    this.books$,
    this.searchFilters$
  ]).pipe(
    map(([books, filters]) => this.applyFilters(books, filters)),
    shareReplay(1)
  );

  public booksWithAvailability$ = this.filteredBooks$.pipe(
    map(books => books.map(book => this.enhanceBookWithAvailability(book))),
    shareReplay(1)
  );

  constructor(private apiService: ApiService) {}

  // Load all books
  loadBooks(): void {
    this.loadingSubject.next(true);
    
    this.apiService.getAllBooks().pipe(
      tap(() => this.loadingSubject.next(false))
    ).subscribe({
      next: (books) => this.booksSubject.next(books),
      error: (error) => {
        this.loadingSubject.next(false);
        console.error('Failed to load books:', error);
      }
    });
  }

  // Create new book
  createBook(bookRequest: CreateBookRequest): Observable<Book> {
    this.loadingSubject.next(true);
    
    return this.apiService.createBook(bookRequest).pipe(
      tap(() => {
        this.loadingSubject.next(false);
        this.loadBooks(); // Refresh list
      })
    );
  }

  // Update search filters
  updateSearchFilters(filters: Partial<SearchFilters>): void {
    const currentFilters = this.searchFiltersSubject.value;
    this.searchFiltersSubject.next({ ...currentFilters, ...filters });
  }

  // Clear search filters
  clearSearchFilters(): void {
    this.searchFiltersSubject.next({});
  }

  // Private methods
  private applyFilters(books: Book[], filters: SearchFilters): Book[] {
    let filtered = [...books];

    if (filters.query) {
      const query = filters.query.toLowerCase();
      filtered = filtered.filter(book => 
        book.title?.toLowerCase().includes(query) || 
        book.author?.toLowerCase().includes(query)
      );
    }

    if (filters.author) {
      filtered = filtered.filter(book => 
        book.author?.toLowerCase().includes(filters.author!.toLowerCase())
      );
    }

    if (filters.availability && filters.availability !== 'all') {
      filtered = filtered.filter(book => {
        const available = (book.availableCopies || 0) > 0;
        return filters.availability === 'available' ? available : !available;
      });
    }

    // Apply sorting
    if (filters.sortBy) {
      filtered.sort((a, b) => {
        const aVal = a[filters.sortBy as keyof Book] as string || '';
        const bVal = b[filters.sortBy as keyof Book] as string || '';
        const comparison = aVal.localeCompare(bVal);
        return filters.sortOrder === 'desc' ? -comparison : comparison;
      });
    }

    return filtered;
  }

  private enhanceBookWithAvailability(book: Book): BookWithAvailability {
    const available = (book.availableCopies || 0);
    const total = (book.totalCopies || 0);
    
    let availabilityStatus: 'available' | 'limited' | 'unavailable';
    
    if (available === 0) {
      availabilityStatus = 'unavailable';
    } else if (available <= total * 0.2) {
      availabilityStatus = 'limited';
    } else {
      availabilityStatus = 'available';
    }

    return {
      ...book,
      isAvailable: available > 0,
      availabilityStatus
    };
  }
}
```

## VALIDATION & TESTING

### **11. Test Generated Client**
Create `src/app/core/services/api.service.spec.ts`:
```typescript
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';
import { Book, CreateBookRequest } from '../models/api.types';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });

    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch all books', () => {
    const mockBooks: Book[] = [
      { id: '1', title: 'Test Book 1', author: 'Author 1', totalCopies: 5, availableCopies: 3 }
    ];

    service.getAllBooks().subscribe(books => {
      expect(books).toEqual(mockBooks);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/books');
    expect(req.request.method).toBe('GET');
    req.flush(mockBooks);
  });

  it('should handle API errors gracefully', () => {
    service.getAllBooks().subscribe({
      next: () => fail('Expected error'),
      error: (error) => {
        expect(error.message).toContain('Server Error');
      }
    });

    const req = httpMock.expectOne('http://localhost:8080/api/books');
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
  });
});
```

### **12. Usage Instructions**

After running this command, you can use the generated API client in your components:

```typescript
// In a component
constructor(private bookFacade: BookFacadeService) {}

ngOnInit(): void {
  this.bookFacade.loadBooks();
  this.books$ = this.bookFacade.booksWithAvailability$;
  this.loading$ = this.bookFacade.loading$;
}
```

## REGENERATION WORKFLOW

### **Update API Client**
When your backend API changes:
```bash
# Regenerate client from running backend
/generate-api-client

# Or from local OpenAPI file
/generate-api-client ./openapi.json

# Or use npm script
npm run api:generate
```

## QUALITY ASSURANCE

### **Generated Code Validation**
- ✅ All TypeScript interfaces generated
- ✅ Angular services with proper DI
- ✅ HTTP client integration
- ✅ Error handling implemented
- ✅ Type safety maintained
- ✅ Observable patterns used
- ✅ Retry logic for failed requests

### **Integration Testing**
- Test generated services with HttpClientTestingModule
- Validate request/response mapping
- Ensure error handling works correctly
- Verify type safety with backend contracts

Your API client is now generated and ready to use with full TypeScript support, error handling, and Angular integration!