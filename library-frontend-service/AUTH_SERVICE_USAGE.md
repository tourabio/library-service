# AuthService Usage Guide

## Overview

The `AuthService` is an Angular service that handles authentication for the library management system. It integrates with the Quarkus backend's GET `/members/authenticate` endpoint to authenticate users using their name and email.

## Features

-  **Login/Authentication**: Authenticate users with name and email
-  **State Management**: Reactive state management using RxJS BehaviorSubject
-  **Persistent Storage**: Stores authentication state in localStorage/sessionStorage
-  **Error Handling**: Comprehensive error handling with retry logic
-  **Type Safety**: Full TypeScript interfaces and type safety
-  **Logout**: Clean logout with state cleanup
-  **Auth Verification**: Optional backend verification of current authentication

## Installation & Setup

The service is already integrated into the project. To use it, simply inject it into your components:

```typescript
import { AuthService } from './core/services/auth.service';

constructor(private authService: AuthService) {}
```

## Basic Usage

### 1. Login

```typescript
import { AuthService, AuthenticationRequest } from './core/services/auth.service';

// In your component
login() {
  const credentials: AuthenticationRequest = {
    name: 'John Doe',
    email: 'john.doe@example.com'
  };

  this.authService.login(credentials).subscribe({
    next: (response) => {
      console.log('Login successful:', response.member);
      // Handle successful authentication
    },
    error: (error) => {
      console.error('Login failed:', error.message);
      // Handle authentication failure
    }
  });
}
```

### 2. Check Authentication Status

```typescript
// Synchronous check
const isAuthenticated = this.authService.isAuthenticated();

// Reactive check (recommended)
this.authService.isAuthenticated$.subscribe(isAuth => {
  console.log('Authentication status:', isAuth);
});
```

### 3. Get Current Member

```typescript
// Synchronous
const currentMember = this.authService.getCurrentMember();

// Reactive (recommended)
this.authService.currentMember$.subscribe(member => {
  if (member) {
    console.log('Current member:', member.name, member.email);
  }
});
```

### 4. Logout

```typescript
// Simple logout
this.authService.logout();
console.log('User logged out');
```

### 5. Verify Authentication

```typescript
// Verify current authentication with backend
this.authService.verifyAuthentication().subscribe({
  next: (isValid) => {
    console.log('Auth verification:', isValid);
  },
  error: (error) => {
    console.error('Verification failed:', error);
    // User will be automatically logged out on verification failure
  }
});
```

## Complete Component Example

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AuthService, AuthenticationRequest } from './core/services/auth.service';
import { Member } from './core/models/member.model';

@Component({
  selector: 'app-my-component',
  template: `
    <div *ngIf="!isAuthenticated">
      <button (click)="login()">Login</button>
    </div>
    
    <div *ngIf="isAuthenticated && currentMember">
      <h3>Welcome, {{ currentMember.name }}!</h3>
      <p>Email: {{ currentMember.email }}</p>
      <button (click)="logout()">Logout</button>
    </div>
  `
})
export class MyComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  isAuthenticated = false;
  currentMember: Member | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Subscribe to authentication state changes
    this.authService.isAuthenticated$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isAuth => {
        this.isAuthenticated = isAuth;
      });

    // Subscribe to current member changes
    this.authService.currentMember$
      .pipe(takeUntil(this.destroy$))
      .subscribe(member => {
        this.currentMember = member;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  login(): void {
    const credentials: AuthenticationRequest = {
      name: 'John Doe',
      email: 'john.doe@example.com'
    };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        console.log('Authentication successful');
      },
      error: (error) => {
        alert('Login failed: ' + error.message);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
```

## API Reference

### Methods

| Method | Description | Returns |
|--------|-------------|---------|
| `login(request: AuthenticationRequest)` | Authenticate user with backend | `Observable<AuthenticationResponse>` |
| `logout()` | Logout current user | `void` |
| `isAuthenticated()` | Check if user is authenticated (sync) | `boolean` |
| `getCurrentMember()` | Get current authenticated member (sync) | `Member \| null` |
| `getAuthState()` | Get current authentication state | `AuthState` |
| `checkAuthStatus()` | Get authentication status as observable | `Observable<boolean>` |
| `verifyAuthentication()` | Verify current auth with backend | `Observable<boolean>` |

### Observables

| Observable | Description | Type |
|------------|-------------|------|
| `authState$` | Complete authentication state | `Observable<AuthState>` |
| `isAuthenticated$` | Authentication status only | `Observable<boolean>` |
| `currentMember$` | Current member information | `Observable<Member \| null>` |

### Interfaces

```typescript
interface AuthenticationRequest {
  readonly name: string;
  readonly email: string;
}

interface AuthenticationResponse {
  readonly member: Member;
  readonly authenticated: boolean;
}

interface AuthState {
  readonly isAuthenticated: boolean;
  readonly member: Member | null;
  readonly token?: string;
}
```

## Error Handling

The service provides comprehensive error handling:

- **400 Bad Request**: Invalid credentials provided
- **401 Unauthorized**: Invalid name or email
- **403 Forbidden**: Account access denied
- **404 Not Found**: Member with credentials does not exist
- **408 Timeout**: Authentication server not responding
- **429 Too Many Requests**: Rate limiting
- **500 Server Error**: Authentication service unavailable
- **503 Service Unavailable**: Service temporarily down

## Storage Strategy

- **Development**: Uses `localStorage` for persistent sessions
- **Production**: Uses `sessionStorage` for security (configurable)
- **Fallback**: Gracefully handles storage failures
- **Recovery**: Automatically loads saved state on service initialization

## Integration with Backend

The service integrates with your Quarkus backend via:

```
GET /members/authenticate?name={name}&email={email}
```

Expected response format:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

## Best Practices

1. **Always use reactive patterns** with observables rather than synchronous methods
2. **Handle errors gracefully** by subscribing to error callbacks
3. **Clean up subscriptions** using `takeUntil` pattern
4. **Check authentication on route guards** for protected routes
5. **Verify authentication periodically** for sensitive operations
6. **Use type safety** with provided interfaces

## Testing

The service includes comprehensive unit tests covering:

-  Authentication success/failure scenarios
-  State management and persistence
-  Error handling for all HTTP status codes
-  Observable streams and reactive patterns
-  Storage management and recovery
-  Logout and cleanup functionality

Run tests with:
```bash
npm test -- --testPathPatterns=auth.service.spec.ts
```