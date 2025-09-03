---
allowed-tools: Bash(ng generate:*), Edit(src/**), Read(src/app/core/models/**), Read(src/environments/**)
argument-hint: [service-name] [feature-module]
description: Generate Angular service with Quarkus backend integration and proper DTOs
model: claude-sonnet-4-20250514
---
Generate a new Angular service for the library management system:

**Service**: $1
**Feature Module**: $2

BACKEND CONTEXT:
- Framework: Quarkus 3.24.5 with JAX-RS REST API
- Base URL: /api (http://localhost:8080/api for development)
- Transfer Objects: BookTO, MemberTO, LoanTO, LoanRequestTO
- Entity IDs: Use number type (Long mapped from backend)
- Status Enum: LoanStatus (ACTIVE, RETURNED, OVERDUE only)

GENERATION PROCESS:
1. Create service: `ng generate service features/$2/services/$1`
2. Import proper DTOs from core/models
3. Implement CRUD operations for Quarkus endpoints
4. Use HttpClient with correct /api base path
5. Add backend-aligned error handling
6. Write tests with proper DTO mocking

BACKEND API PATTERNS:
- GET /api/{entity} - Get all entities (returns DTO array)
- GET /api/{entity}/{id} - Get entity by ID (returns DTO)
- POST /api/{entity} - Create entity (accepts DTO, returns DTO)
- PUT /api/{entity}/{id} - Update entity (accepts DTO, returns DTO)
- DELETE /api/{entity}/{id} - Delete entity (returns void)

REQUIRED FEATURES:
- Use BookTO/MemberTO/LoanTO interfaces
- Number type for all entity IDs (Long backend → number frontend)
- Date handling for LocalDate fields
- Loading state management with BehaviorSubject
- Retry logic for transient failures
- Backend exception error mapping

QUARKUS-SPECIFIC INTEGRATION:
- API base path: environment.apiUrl + '/api'
- Content-Type: application/json
- Error status codes: 400 (validation), 404 (not found), 409 (conflict)
- Success responses: Direct DTO objects (no wrapper)
- Date serialization: ISO string format from LocalDate

TESTING REQUIREMENTS:
- Mock HTTP calls to /api/* endpoints
- Test with proper DTO objects
- Verify number ID handling
- Test Quarkus error response format
- >80% code coverage

EXAMPLE SERVICE TEMPLATE:
```typescript
@Injectable({ providedIn: 'root' })
export class ${1}Service {
  private readonly apiUrl = \`\${environment.apiUrl}/api/${1.toLowerCase()}s\`;
  
  constructor(private http: HttpClient) {}
  
  getAll(): Observable<${1}TO[]> {
    return this.http.get<${1}TO[]>(this.apiUrl);
  }
  
  getById(id: number): Observable<${1}TO> {
    return this.http.get<${1}TO>(\`\${this.apiUrl}/\${id}\`);
  }
  
  create(entity: ${1}TO): Observable<${1}TO> {
    return this.http.post<${1}TO>(this.apiUrl, entity);
  }
  
  update(id: number, entity: ${1}TO): Observable<${1}TO> {
    return this.http.put<${1}TO>(\`\${this.apiUrl}/\${id}\`, entity);
  }
  
  delete(id: number): Observable<void> {
    return this.http.delete<void>(\`\${this.apiUrl}/\${id}\`);
  }
}
```