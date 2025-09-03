import { Component, OnInit, ChangeDetectionStrategy, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs/operators';

import { AuthService, AuthenticationRequest } from '../../../../core/services/auth.service';

/**
 * Login component for library management system authentication
 * @component LoginComponent
 * @description Provides member authentication using name and email credentials
 * @example
 * <app-login></app-login>
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  public loginForm!: FormGroup;
  public isLoading = false;
  public hidePassword = true;

  ngOnInit(): void {
    this.initializeForm();
    this.checkExistingAuth();
  }

  /**
   * Initialize the reactive form with validation rules
   */
  private initializeForm(): void {
    this.loginForm = this.fb.group({
      name: [
        '', 
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(100),
          Validators.pattern(/^[a-zA-Z\s'-]+$/)
        ]
      ],
      email: [
        '', 
        [
          Validators.required,
          Validators.email,
          Validators.maxLength(255)
        ]
      ]
    });
  }

  /**
   * Check if user is already authenticated and redirect if necessary
   */
  private checkExistingAuth(): void {
    this.authService.isAuthenticated$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(isAuthenticated => {
        if (isAuthenticated) {
          this.router.navigate(['/books']);
        }
      });
  }

  /**
   * Handle form submission for member authentication
   */
  public onSubmit(): void {
    if (this.loginForm.valid && !this.isLoading) {
      this.performLogin();
    } else {
      this.markFormGroupTouched();
    }
  }

  /**
   * Perform authentication request
   */
  private performLogin(): void {
    const request: AuthenticationRequest = {
      name: this.loginForm.get('name')?.value.trim(),
      email: this.loginForm.get('email')?.value.trim()
    };

    this.isLoading = true;
    this.loginForm.disable();

    this.authService.login(request)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => {
          this.isLoading = false;
          this.loginForm.enable();
        })
      )
      .subscribe({
        next: (response) => {
          this.handleLoginSuccess(response.member.name);
        },
        error: (error) => {
          this.handleLoginError(error.message);
        }
      });
  }

  /**
   * Handle successful authentication
   */
  private handleLoginSuccess(memberName: string): void {
    this.snackBar.open(
      `Welcome back, ${memberName}!`, 
      'Close', 
      {
        duration: 3000,
        panelClass: ['success-snackbar'],
        horizontalPosition: 'end',
        verticalPosition: 'top'
      }
    );
    
    // Navigate to books page
    this.router.navigate(['/books']);
  }

  /**
   * Handle authentication errors
   */
  private handleLoginError(errorMessage: string): void {
    this.snackBar.open(
      errorMessage || 'Login failed. Please check your credentials.',
      'Close',
      {
        duration: 5000,
        panelClass: ['error-snackbar'],
        horizontalPosition: 'end',
        verticalPosition: 'top'
      }
    );

    // Reset form on error
    this.loginForm.get('name')?.setErrors(null);
    this.loginForm.get('email')?.setErrors(null);
  }

  /**
   * Mark all form fields as touched to trigger validation messages
   */
  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Get form control error message for display
   */
  public getErrorMessage(fieldName: string): string {
    const control = this.loginForm.get(fieldName);
    
    if (control?.hasError('required')) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
    }
    
    if (control?.hasError('email')) {
      return 'Please enter a valid email address';
    }
    
    if (control?.hasError('minlength')) {
      const minLength = control.errors?.['minlength']?.requiredLength;
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must be at least ${minLength} characters`;
    }
    
    if (control?.hasError('maxlength')) {
      const maxLength = control.errors?.['maxlength']?.requiredLength;
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must not exceed ${maxLength} characters`;
    }
    
    if (control?.hasError('pattern')) {
      return 'Name can only contain letters, spaces, hyphens, and apostrophes';
    }
    
    return '';
  }

  /**
   * Check if form field has error and is touched
   */
  public hasError(fieldName: string): boolean {
    const control = this.loginForm.get(fieldName);
    return !!(control?.invalid && (control?.dirty || control?.touched));
  }

  /**
   * Quick login for demo purposes (can be removed in production)
   */
  public quickLogin(memberType: 'student' | 'faculty'): void {
    if (memberType === 'student') {
      this.loginForm.patchValue({
        name: 'John Doe',
        email: 'john.doe@example.com'
      });
    } else {
      this.loginForm.patchValue({
        name: 'Dr. Jane Smith',
        email: 'jane.smith@university.edu'
      });
    }
  }
}