import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

/**
 * NotFoundComponent - A beautiful 404 error page for the Library Management System
 * @component NotFoundComponent
 * @description Displays a user-friendly 404 error page with library theme styling
 * and navigation options when users encounter a non-existent route.
 * @example
 * <app-not-found></app-not-found>
 */
@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotFoundComponent {
  public readonly notFoundImagePath = '/notfound.png';

  constructor(private router: Router) {}

  /**
   * Navigates the user to the books page (main page after login)
   * @returns void
   */
  public navigateToHome(): void {
    this.router.navigate(['/books']).catch(error => {
      console.error('Navigation error:', error);
      // Fallback to login if books navigation fails
      this.router.navigate(['/auth/login']);
    });
  }

  /**
   * Navigates the user back to the previous page in browser history
   * @returns void
   */
  public goBack(): void {
    window.history.back();
  }
}