import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, retry, tap, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

// Import existing models
import { Member, AuthenticationTO } from '../models/member.model';

// Authentication specific interfaces
export interface AuthenticationRequest {
  readonly name: string;
  readonly email: string;
}

export interface AuthenticationResponse {
  readonly member: Member;
  readonly authenticated: boolean;
}

export interface AuthState {
  readonly isAuthenticated: boolean;
  readonly member: Member | null;
  readonly token?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/members`;
  private readonly STORAGE_KEY = 'library_auth_state';
  
  // BehaviorSubject to manage authentication state
  private readonly authStateSubject = new BehaviorSubject<AuthState>(this.loadAuthState());
  public readonly authState$ = this.authStateSubject.asObservable();
  
  // Convenience observables
  public readonly isAuthenticated$ = this.authState$.pipe(
    map(state => state.isAuthenticated)
  );
  
  public readonly currentMember$ = this.authState$.pipe(
    map(state => state.member)
  );

  constructor(private http: HttpClient) {}

  /**
   * Authenticate member using name and email
   * Calls GET /members/authenticate?name=...&email=...
   */
  login(request: AuthenticationRequest): Observable<AuthenticationResponse> {
    const params = new HttpParams()
      .set('name', request.name)
      .set('email', request.email);

    return this.http.get<AuthenticationTO>(`${this.baseUrl}/authenticate`, { params }).pipe(
      retry(1), // Single retry for authentication
      tap(authTO => {
        // Convert AuthenticationTO to Member for internal use
        const member: Member = {
          id: authTO.id,
          name: authTO.name,
          email: authTO.email
        };
        const authState: AuthState = {
          isAuthenticated: true,
          member: member,
          token: `auth_${member.id}_${Date.now()}` // Simple token for demo
        };
        this.updateAuthState(authState);
      }),
      map(authTO => ({
        member: {
          id: authTO.id,
          name: authTO.name,
          email: authTO.email
        },
        authenticated: true
      })),
      catchError(this.handleError)
    );
  }

  /**
   * Logout current user
   */
  logout(): void {
    const authState: AuthState = {
      isAuthenticated: false,
      member: null
    };
    this.updateAuthState(authState);
    this.clearStoredAuthState();
  }

  /**
   * Check if user is currently authenticated
   */
  isAuthenticated(): boolean {
    return this.authStateSubject.value.isAuthenticated;
  }

  /**
   * Get current authenticated member
   */
  getCurrentMember(): Member | null {
    return this.authStateSubject.value.member;
  }

  /**
   * Get current authentication state
   */
  getAuthState(): AuthState {
    return this.authStateSubject.value;
  }

  /**
   * Check authentication status without making HTTP call
   */
  checkAuthStatus(): Observable<boolean> {
    return this.isAuthenticated$;
  }

  /**
   * Verify current authentication with backend (optional refresh)
   */
  verifyAuthentication(): Observable<boolean> {
    const currentMember = this.getCurrentMember();
    
    if (!currentMember) {
      return throwError(() => new Error('No authenticated member found'));
    }

    return this.login({ 
      name: currentMember.name, 
      email: currentMember.email 
    }).pipe(
      map(response => response.authenticated),
      catchError(error => {
        // If verification fails, logout
        this.logout();
        return throwError(() => error);
      })
    );
  }

  /**
   * Update authentication state and notify subscribers
   */
  private updateAuthState(authState: AuthState): void {
    this.authStateSubject.next(authState);
    this.storeAuthState(authState);
  }

  /**
   * Load authentication state from storage
   */
  private loadAuthState(): AuthState {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY) || sessionStorage.getItem(this.STORAGE_KEY);
      if (stored) {
        const parsedState = JSON.parse(stored) as AuthState;
        return {
          isAuthenticated: parsedState.isAuthenticated || false,
          member: parsedState.member || null,
          token: parsedState.token
        };
      }
    } catch (error) {
      console.warn('Failed to load auth state from storage:', error);
    }
    
    return {
      isAuthenticated: false,
      member: null
    };
  }

  /**
   * Store authentication state in localStorage (persistent) or sessionStorage
   */
  private storeAuthState(authState: AuthState): void {
    try {
      const stateToStore = JSON.stringify(authState);
      // Use localStorage for persistent sessions, sessionStorage for temporary
      if (environment.production) {
        sessionStorage.setItem(this.STORAGE_KEY, stateToStore);
      } else {
        localStorage.setItem(this.STORAGE_KEY, stateToStore);
      }
    } catch (error) {
      console.warn('Failed to store auth state:', error);
    }
  }

  /**
   * Clear stored authentication state
   */
  private clearStoredAuthState(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    sessionStorage.removeItem(this.STORAGE_KEY);
  }

  /**
   * Error handling for authentication requests
   */
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = 'Authentication failed';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error from Quarkus backend
      switch (error.status) {
        case 400:
          errorMessage = 'Bad Request: Invalid credentials provided';
          break;
        case 401:
          errorMessage = 'Unauthorized: Invalid name or email';
          break;
        case 403:
          errorMessage = 'Forbidden: Account access denied';
          break;
        case 404:
          errorMessage = 'Not Found: Member with provided credentials does not exist';
          break;
        case 408:
          errorMessage = 'Request Timeout: Authentication server is not responding';
          break;
        case 429:
          errorMessage = 'Too Many Requests: Please wait before trying again';
          break;
        case 500:
          errorMessage = 'Server Error: Authentication service unavailable';
          break;
        case 503:
          errorMessage = 'Service Unavailable: Authentication service is temporarily down';
          break;
        default:
          errorMessage = `Authentication Error: ${error.status} - ${error.message}`;
      }
    }

    console.error('Authentication Error:', error);
    return throwError(() => new Error(errorMessage));
  };
}