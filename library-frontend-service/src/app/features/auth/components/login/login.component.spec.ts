import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError, BehaviorSubject } from 'rxjs';

import { LoginComponent } from './login.component';
import { AuthService, AuthenticationRequest, AuthenticationResponse } from '../../../../core/services/auth.service';
import { Member } from '../../../../core/models/member.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn().mockReturnValue(of({ member: { name: 'Test User' }, authenticated: true })),
      isAuthenticated$: new BehaviorSubject(false)
    };
    const routerMock = { navigate: jest.fn() };
    const snackBarMock = { open: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: snackBarMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.loginForm.get('name')?.value).toBe('');
    expect(component.loginForm.get('email')?.value).toBe('');
  });

  it('should validate required fields', () => {
    const nameControl = component.loginForm.get('name');
    const emailControl = component.loginForm.get('email');
    
    expect(nameControl?.hasError('required')).toBeTruthy();
    expect(emailControl?.hasError('required')).toBeTruthy();
  });

  it('should validate email format', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBeTruthy();
    
    emailControl?.setValue('valid@email.com');
    expect(emailControl?.hasError('email')).toBeFalsy();
  });

  it('should populate demo login data', () => {
    component.quickLogin('student');
    expect(component.loginForm.get('name')?.value).toBe('John Doe');
    expect(component.loginForm.get('email')?.value).toBe('john.doe@example.com');
  });
});