import { Component, OnInit, ChangeDetectionStrategy, DestroyRef, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, catchError, of } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { Member } from '../../models/member.model';

/**
 * Main layout component for authenticated library management system pages
 * @component MainLayoutComponent
 * @description Provides header with user info, logout functionality, and router outlet
 * @example
 * <app-main-layout></app-main-layout>
 */
@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MainLayoutComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  // Reactive state using Angular signals
  public readonly currentMember = signal<Member | null>(null);
  public readonly isLoading = signal<boolean>(false);
  public readonly isAuthenticated = signal<boolean>(false);

  // Computed properties
  public readonly memberDisplayName = computed(() => {
    const member = this.currentMember();
    return member ? this.formatMemberName(member.name) : 'Guest';
  });

  public readonly memberInitials = computed(() => {
    const member = this.currentMember();
    return member ? this.getInitials(member.name) : 'G';
  });

  ngOnInit(): void {
    this.initializeAuthState();
  }

  /**
   * Initialize authentication state and subscribe to changes
   */
  private initializeAuthState(): void {
    // Subscribe to authentication state changes
    this.authService.authState$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(authState => {
        this.isAuthenticated.set(authState.isAuthenticated);
        this.currentMember.set(authState.member);
        
        // Redirect to login if not authenticated
        if (!authState.isAuthenticated) {
          this.router.navigate(['/auth/login']);
        }
      });
  }

  /**
   * Handle user logout
   */
  public onLogout(): void {
    this.isLoading.set(true);
    
    try {
      // Call auth service logout
      this.authService.logout();
      
      // Show success message
      this.showSnackBar(
        'Successfully logged out. See you next time!',
        'success-snackbar',
        2000
      );
      
      // Navigate to login page
      this.router.navigate(['/auth/login']);
      
    } catch (error) {
      console.error('Logout error:', error);
      this.showSnackBar(
        'Logout completed with some issues. Please clear your browser cache if problems persist.',
        'error-snackbar',
        4000
      );
      
      // Still navigate to login even if there's an error
      this.router.navigate(['/auth/login']);
    } finally {
      this.isLoading.set(false);
    }
  }

  /**
   * Handle profile menu actions
   */
  public onViewProfile(): void {
    // Navigate to profile page (if implemented)
    this.showSnackBar('Profile page coming soon!', 'info-snackbar', 2000);
  }

  /**
   * Handle settings menu action
   */
  public onSettings(): void {
    // Navigate to settings page (if implemented)
    this.showSnackBar('Settings page coming soon!', 'info-snackbar', 2000);
  }

  /**
   * Verify authentication status (for refresh/reload scenarios)
   */
  public refreshAuthStatus(): void {
    this.isLoading.set(true);
    
    this.authService.verifyAuthentication()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false)),
        catchError(error => {
          console.warn('Auth verification failed:', error);
          this.showSnackBar(
            'Session verification failed. Please log in again.',
            'error-snackbar',
            3000
          );
          return of(false);
        })
      )
      .subscribe(isValid => {
        if (!isValid) {
          this.router.navigate(['/auth/login']);
        }
      });
  }

  /**
   * Format member name for display (handle long names)
   */
  private formatMemberName(name: string): string {
    if (!name) return 'Guest';
    
    // If name is too long, show first name + last initial
    if (name.length > 20) {
      const parts = name.trim().split(' ');
      if (parts.length > 1) {
        return `${parts[0]} ${parts[parts.length - 1].charAt(0)}.`;
      }
      return name.substring(0, 17) + '...';
    }
    
    return name;
  }

  /**
   * Get initials from member name
   */
  private getInitials(name: string): string {
    if (!name) return 'G';
    
    const parts = name.trim().split(' ').filter(part => part.length > 0);
    
    if (parts.length >= 2) {
      return `${parts[0].charAt(0)}${parts[parts.length - 1].charAt(0)}`.toUpperCase();
    }
    
    return parts[0]?.charAt(0).toUpperCase() || 'G';
  }

  /**
   * Show snackbar with consistent styling
   */
  private showSnackBar(message: string, panelClass: string, duration: number = 3000): void {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: [panelClass],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }

  /**
   * Handle navigation to different sections
   */
  public navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  /**
   * Check if current route is active
   */
  public isRouteActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }
}