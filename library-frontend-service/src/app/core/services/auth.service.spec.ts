import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';

import { AuthService, AuthenticationRequest, AuthenticationResponse, AuthState } from './auth.service';
import { Member } from '../models/member.model';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  
  const mockMember: Member = {
    id: 1,
    name: 'John Doe',
    email: 'john.doe@example.com'
  };

  const mockAuthRequest: AuthenticationRequest = {
    name: 'John Doe',
    email: 'john.doe@example.com'
  };

  // Helper function to match authenticate requests
  const matchAuthRequest = (name: string, email: string) => {
    return (req: HttpRequest<any>) => {
      return req.url === `${environment.apiUrl}/members/authenticate` &&
             req.method === 'GET' &&
             req.params.get('name') === name &&
             req.params.get('email') === email;
    };
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    
    // Clear localStorage before each test
    localStorage.clear();
    sessionStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
    sessionStorage.clear();
  });

  describe('Service Initialization', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should initialize with unauthenticated state', () => {
      expect(service.isAuthenticated()).toBeFalsy();
      expect(service.getCurrentMember()).toBeNull();
    });

    it('should provide observable streams', (done) => {
      service.isAuthenticated$.subscribe(isAuth => {
        expect(isAuth).toBeFalsy();
        done();
      });
    });
  });

  describe('Authentication', () => {
    it('should authenticate successfully with valid credentials', (done) => {
      service.login(mockAuthRequest).subscribe({
        next: (response: AuthenticationResponse) => {
          expect(response.authenticated).toBeTruthy();
          expect(response.member).toEqual(mockMember);
          expect(service.isAuthenticated()).toBeTruthy();
          expect(service.getCurrentMember()).toEqual(mockMember);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(mockMember);
    });

    it('should handle authentication failure with 404', (done) => {
      service.login(mockAuthRequest).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.message).toContain('Member with provided credentials does not exist');
          expect(service.isAuthenticated()).toBeFalsy();
          done();
        }
      });

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(null, { status: 404, statusText: 'Not Found' });
    });

    it('should handle authentication failure with 401', (done) => {
      service.login(mockAuthRequest).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.message).toContain('Invalid name or email');
          done();
        }
      });

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(null, { status: 401, statusText: 'Unauthorized' });
    });

    it('should retry authentication once on failure', () => {
      service.login(mockAuthRequest).subscribe();

      // First request fails
      const req1 = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req1.error(new ErrorEvent('Network error'));

      // Second request (retry) succeeds
      const req2 = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req2.flush(mockMember);
    });
  });

  describe('Logout', () => {
    beforeEach((done) => {
      // Authenticate first
      service.login(mockAuthRequest).subscribe(() => done());

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(mockMember);
    });

    it('should logout successfully', () => {
      expect(service.isAuthenticated()).toBeTruthy();
      
      service.logout();
      
      expect(service.isAuthenticated()).toBeFalsy();
      expect(service.getCurrentMember()).toBeNull();
    });

    it('should clear stored authentication state on logout', () => {
      service.logout();
      
      expect(localStorage.getItem('library_auth_state')).toBeNull();
      expect(sessionStorage.getItem('library_auth_state')).toBeNull();
    });

    it('should notify subscribers of logout', (done) => {
      let callCount = 0;
      
      service.authState$.subscribe((state: AuthState) => {
        callCount++;
        if (callCount === 2) { // Second call after logout
          expect(state.isAuthenticated).toBeFalsy();
          expect(state.member).toBeNull();
          done();
        }
      });

      service.logout();
    });
  });

  describe('Authentication State Management', () => {
    it('should update auth state on successful login', (done) => {
      service.authState$.subscribe((state: AuthState) => {
        if (state.isAuthenticated) {
          expect(state.member).toEqual(mockMember);
          expect(state.token).toContain(`auth_${mockMember.id}`);
          done();
        }
      });

      service.login(mockAuthRequest).subscribe();

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(mockMember);
    });

    it('should provide current member observable', (done) => {
      service.currentMember$.subscribe((member) => {
        if (member) {
          expect(member).toEqual(mockMember);
          done();
        }
      });

      service.login(mockAuthRequest).subscribe();

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(mockMember);
    });
  });

  describe('Storage Management', () => {
    it('should store auth state in localStorage in development', (done) => {
      service.login(mockAuthRequest).subscribe(() => {
        const stored = localStorage.getItem('library_auth_state');
        expect(stored).toBeTruthy();
        
        const parsedState = JSON.parse(stored!) as AuthState;
        expect(parsedState.isAuthenticated).toBeTruthy();
        expect(parsedState.member).toEqual(mockMember);
        done();
      });

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(mockMember);
    });

    it('should load auth state from storage on service creation', () => {
      const authState: AuthState = {
        isAuthenticated: true,
        member: mockMember,
        token: 'test_token'
      };
      
      localStorage.setItem('library_auth_state', JSON.stringify(authState));
      
      // Create new service instance
      const newService = TestBed.inject(AuthService);
      
      expect(newService.isAuthenticated()).toBeTruthy();
      expect(newService.getCurrentMember()).toEqual(mockMember);
    });

    it('should handle corrupted storage gracefully', () => {
      localStorage.setItem('library_auth_state', 'invalid-json');
      
      // Create new service instance
      const newService = TestBed.inject(AuthService);
      
      expect(newService.isAuthenticated()).toBeFalsy();
      expect(newService.getCurrentMember()).toBeNull();
    });
  });

  describe('Authentication Verification', () => {
    beforeEach((done) => {
      // Set up authenticated state
      service.login(mockAuthRequest).subscribe(() => done());

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.flush(mockMember);
    });

    it('should verify authentication successfully', (done) => {
      service.verifyAuthentication().subscribe((isAuthenticated) => {
        expect(isAuthenticated).toBeTruthy();
        done();
      });

      const req = httpMock.expectOne(matchAuthRequest(mockMember.name, mockMember.email));
      req.flush(mockMember);
    });

    it('should logout on verification failure', (done) => {
      service.verifyAuthentication().subscribe({
        next: () => done.fail('Should have failed'),
        error: () => {
          expect(service.isAuthenticated()).toBeFalsy();
          done();
        }
      });

      const req = httpMock.expectOne(matchAuthRequest(mockMember.name, mockMember.email));
      req.flush(null, { status: 401, statusText: 'Unauthorized' });
    });

    it('should fail verification when no member is authenticated', (done) => {
      service.logout();

      service.verifyAuthentication().subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.message).toContain('No authenticated member found');
          done();
        }
      });
    });
  });

  describe('Error Handling', () => {
    it('should handle different HTTP error codes appropriately', () => {
      const errorCases = [
        { status: 400, expectedMessage: 'Invalid credentials provided' },
        { status: 403, expectedMessage: 'Account access denied' },
        { status: 408, expectedMessage: 'Authentication server is not responding' },
        { status: 429, expectedMessage: 'Too Many Requests' },
        { status: 500, expectedMessage: 'Authentication service unavailable' },
        { status: 503, expectedMessage: 'Authentication service is temporarily down' }
      ];

      errorCases.forEach((errorCase, index) => {
        service.login(mockAuthRequest).subscribe({
          next: () => fail(`Test ${index} should have failed`),
          error: (error) => {
            expect(error.message).toContain(errorCase.expectedMessage);
          }
        });

        const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
        req.flush(null, { status: errorCase.status, statusText: 'Error' });
      });
    });

    it('should handle network errors', (done) => {
      service.login(mockAuthRequest).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.message).toContain('Client Error');
          done();
        }
      });

      const req = httpMock.expectOne(matchAuthRequest(mockAuthRequest.name, mockAuthRequest.email));
      req.error(new ErrorEvent('Network error', { message: 'Connection failed' }));
    });
  });

  describe('Utility Methods', () => {
    it('should check authentication status without HTTP call', (done) => {
      service.checkAuthStatus().subscribe((isAuth) => {
        expect(isAuth).toBeFalsy();
        done();
      });
    });

    it('should return current auth state', () => {
      const state = service.getAuthState();
      expect(state.isAuthenticated).toBeFalsy();
      expect(state.member).toBeNull();
    });
  });
});