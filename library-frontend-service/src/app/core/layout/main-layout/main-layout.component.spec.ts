import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { BehaviorSubject, of, throwError } from 'rxjs';

import { MainLayoutComponent } from './main-layout.component';
import { AuthService, AuthState } from '../../services/auth.service';
import { Member } from '../../models/member.model';

describe('MainLayoutComponent', () => {
  let component: MainLayoutComponent;
  let fixture: ComponentFixture<MainLayoutComponent>;
  let mockAuthService: jest.Mocked<AuthService>;
  let mockRouter: jest.Mocked<Router>;
  let mockSnackBar: jest.Mocked<MatSnackBar>;
  let authStateSubject: BehaviorSubject<AuthState>;

  // Test data
  const mockMember: Member = {
    id: 1,
    name: 'John Doe',
    email: 'john.doe@example.com'
  };

  const mockAuthenticatedState: AuthState = {
    isAuthenticated: true,
    member: mockMember,
    token: 'test-token'
  };

  const mockUnauthenticatedState: AuthState = {
    isAuthenticated: false,
    member: null
  };

  beforeEach(async () => {
    // Create auth state subject for testing
    authStateSubject = new BehaviorSubject<AuthState>(mockUnauthenticatedState);

    // Create spies using Jest
    mockAuthService = {
      logout: jest.fn(),
      verifyAuthentication: jest.fn(),
      authState$: authStateSubject.asObservable()
    } as any;

    mockRouter = {
      navigate: jest.fn(),
      url: '/dashboard'
    } as any;

    mockSnackBar = {
      open: jest.fn()
    } as any;

    await TestBed.configureTestingModule({
      imports: [
        MainLayoutComponent,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MainLayoutComponent);
    component = fixture.componentInstance;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with unauthenticated state', () => {
      fixture.detectChanges();

      expect(component.isAuthenticated()).toBeFalsy();
      expect(component.currentMember()).toBeNull();
      expect(component.memberDisplayName()).toBe('Guest');
      expect(component.memberInitials()).toBe('G');
    });

    it('should redirect to login when not authenticated', () => {
      fixture.detectChanges();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should update state when authenticated', () => {
      authStateSubject.next(mockAuthenticatedState);
      fixture.detectChanges();

      expect(component.isAuthenticated()).toBeTruthy();
      expect(component.currentMember()).toEqual(mockMember);
      expect(component.memberDisplayName()).toBe('John Doe');
      expect(component.memberInitials()).toBe('JD');
    });
  });

  describe('Authentication State Management', () => {
    beforeEach(() => {
      authStateSubject.next(mockAuthenticatedState);
      fixture.detectChanges();
    });

    it('should handle logout successfully', () => {
      component.onLogout();

      expect(component.isLoading()).toBeTruthy();
      expect(mockAuthService.logout).toHaveBeenCalled();
      expect(mockSnackBar.open).toHaveBeenCalledWith(
        'Successfully logged out. See you next time!',
        'Close',
        expect.objectContaining({
          duration: 2000,
          panelClass: ['success-snackbar']
        })
      );
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should handle logout errors gracefully', () => {
      mockAuthService.logout.mockImplementation(() => {
        throw new Error('Logout error');
      });

      component.onLogout();

      expect(mockSnackBar.open).toHaveBeenCalledWith(
        expect.stringContaining('Logout completed with some issues'),
        'Close',
        expect.objectContaining({
          duration: 4000,
          panelClass: ['error-snackbar']
        })
      );
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should refresh authentication status successfully', () => {
      mockAuthService.verifyAuthentication.mockReturnValue(of(true));

      component.refreshAuthStatus();

      expect(component.isLoading()).toBeTruthy();
      expect(mockAuthService.verifyAuthentication).toHaveBeenCalled();
    });

    it('should handle authentication verification failure', () => {
      mockAuthService.verifyAuthentication.mockReturnValue(throwError(() => new Error('Auth failed')));

      component.refreshAuthStatus();

      expect(mockSnackBar.open).toHaveBeenCalledWith(
        'Session verification failed. Please log in again.',
        'Close',
        expect.objectContaining({
          duration: 3000,
          panelClass: ['error-snackbar']
        })
      );
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
    });
  });

  describe('User Interface', () => {
    beforeEach(() => {
      authStateSubject.next(mockAuthenticatedState);
      fixture.detectChanges();
    });

    it('should display user information in header', () => {
      const userAvatar = fixture.debugElement.query(By.css('.user-avatar .avatar-text'));
      const userName = fixture.debugElement.query(By.css('.user-name'));

      expect(userAvatar.nativeElement.textContent.trim()).toBe('JD');
      expect(userName.nativeElement.textContent.trim()).toBe('John Doe');
    });

    it('should display navigation buttons', () => {
      const navButtons = fixture.debugElement.queryAll(By.css('.nav-button'));

      expect(navButtons.length).toBe(4);
      
      const buttonLabels = navButtons.map(btn => 
        btn.query(By.css('.nav-label'))?.nativeElement.textContent.trim()
      ).filter(label => label); // Filter out undefined for mobile view

      expect(buttonLabels).toContain('Dashboard');
      expect(buttonLabels).toContain('Books');
      expect(buttonLabels).toContain('Members');
      expect(buttonLabels).toContain('Loans');
    });

    it('should show guest section when not authenticated', () => {
      authStateSubject.next(mockUnauthenticatedState);
      fixture.detectChanges();

      const guestSection = fixture.debugElement.query(By.css('.guest-section'));
      const userSection = fixture.debugElement.query(By.css('.user-section'));

      expect(guestSection).toBeTruthy();
      expect(userSection).toBeFalsy();
    });

    it('should show loading overlay when loading', () => {
      component.isLoading.set(true);
      fixture.detectChanges();

      const loadingOverlay = fixture.debugElement.query(By.css('.loading-overlay'));
      const mainLayout = fixture.debugElement.query(By.css('.main-layout.loading'));

      expect(loadingOverlay).toBeTruthy();
      expect(mainLayout).toBeTruthy();
    });
  });

  describe('Navigation', () => {
    beforeEach(() => {
      authStateSubject.next(mockAuthenticatedState);
      fixture.detectChanges();
    });

    it('should navigate to specified route', () => {
      component.navigateTo('/books');

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/books']);
    });

    it('should check if route is active', () => {
      mockRouter.url = '/books/list';

      expect(component.isRouteActive('/books')).toBeTruthy();
      expect(component.isRouteActive('/members')).toBeFalsy();
    });

    it('should handle navigation button clicks', () => {
      const dashboardButton = fixture.debugElement.query(
        By.css('.nav-button[aria-label="Navigate to Dashboard"]')
      );

      dashboardButton.triggerEventHandler('click', null);

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/dashboard']);
    });
  });

  describe('Menu Actions', () => {
    beforeEach(() => {
      authStateSubject.next(mockAuthenticatedState);
      fixture.detectChanges();
    });

    it('should show profile coming soon message', () => {
      component.onViewProfile();

      expect(mockSnackBar.open).toHaveBeenCalledWith(
        'Profile page coming soon!',
        'Close',
        expect.objectContaining({
          duration: 2000,
          panelClass: ['info-snackbar']
        })
      );
    });

    it('should show settings coming soon message', () => {
      component.onSettings();

      expect(mockSnackBar.open).toHaveBeenCalledWith(
        'Settings page coming soon!',
        'Close',
        expect.objectContaining({
          duration: 2000,
          panelClass: ['info-snackbar']
        })
      );
    });
  });

  describe('Utility Functions', () => {
    it('should format long member names correctly', () => {
      const longNameMember: Member = {
        id: 2,
        name: 'Alexander Christopher Maximilian Johnson-Smith',
        email: 'alexander@example.com'
      };

      const authState: AuthState = {
        isAuthenticated: true,
        member: longNameMember,
        token: 'test-token'
      };

      authStateSubject.next(authState);
      fixture.detectChanges();

      expect(component.memberDisplayName()).toBe('Alexander S.');
    });

    it('should handle single names for initials', () => {
      const singleNameMember: Member = {
        id: 3,
        name: 'Madonna',
        email: 'madonna@example.com'
      };

      const authState: AuthState = {
        isAuthenticated: true,
        member: singleNameMember,
        token: 'test-token'
      };

      authStateSubject.next(authState);
      fixture.detectChanges();

      expect(component.memberInitials()).toBe('M');
    });

    it('should handle empty names gracefully', () => {
      const emptyNameMember: Member = {
        id: 4,
        name: '',
        email: 'test@example.com'
      };

      const authState: AuthState = {
        isAuthenticated: true,
        member: emptyNameMember,
        token: 'test-token'
      };

      authStateSubject.next(authState);
      fixture.detectChanges();

      expect(component.memberDisplayName()).toBe('Guest');
      expect(component.memberInitials()).toBe('G');
    });
  });

  describe('Accessibility', () => {
    beforeEach(() => {
      authStateSubject.next(mockAuthenticatedState);
      fixture.detectChanges();
    });

    it('should have proper ARIA labels', () => {
      const mainContent = fixture.debugElement.query(By.css('main[role="main"]'));
      const banner = fixture.debugElement.query(By.css('mat-toolbar[role="banner"]'));
      const navigation = fixture.debugElement.query(By.css('nav[aria-label="Main navigation"]'));

      expect(mainContent).toBeTruthy();
      expect(banner).toBeTruthy();
      expect(navigation).toBeTruthy();
    });

    it('should have accessible navigation buttons', () => {
      const navButtons = fixture.debugElement.queryAll(By.css('.nav-button[aria-label]'));

      expect(navButtons.length).toBeGreaterThan(0);
      
      navButtons.forEach(button => {
        expect(button.nativeElement.getAttribute('aria-label')).toBeTruthy();
      });
    });

    it('should have accessible logout button', () => {
      const logoutButton = fixture.debugElement.query(
        By.css('.logout-button[aria-label="Logout from library system"]')
      );

      expect(logoutButton).toBeTruthy();
    });
  });

  describe('Error Handling', () => {
    it('should handle missing member data', () => {
      const invalidAuthState: AuthState = {
        isAuthenticated: true,
        member: null,
        token: 'test-token'
      };

      authStateSubject.next(invalidAuthState);
      fixture.detectChanges();

      expect(component.memberDisplayName()).toBe('Guest');
      expect(component.memberInitials()).toBe('G');
    });

    it('should not crash with undefined member properties', () => {
      const partialMember = { id: 1, name: undefined, email: undefined } as any;
      const authState: AuthState = {
        isAuthenticated: true,
        member: partialMember,
        token: 'test-token'
      };

      authStateSubject.next(authState);

      expect(() => fixture.detectChanges()).not.toThrow();
    });
  });
});