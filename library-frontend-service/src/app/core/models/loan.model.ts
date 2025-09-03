import { BookTO } from './book.model';
import { MemberTO } from './member.model';

// Loan entity matching backend Loan.java
export interface Loan {
  readonly id: number;
  readonly bookId: number;
  readonly memberId: number;
  readonly loanDate: string;
  readonly dueDate: string;
  readonly returnDate?: string;
  readonly status: LoanStatus;
}

// LoanTO matching backend LoanTO record
export interface LoanTO {
  readonly book: BookTO;
  readonly member: MemberTO;
  readonly loanDate: string;
  readonly dueDate: string;
  readonly returnDate?: string;
  readonly status: LoanStatus;
}

// LoanRequestTO matching backend LoanRequestTO record
export interface LoanRequestTO {
  readonly bookId: number;
  readonly memberId: number;
}

// LoanStatus enum matching backend LoanStatus.java
export enum LoanStatus {
  ACTIVE = 'ACTIVE',
  RETURNED = 'RETURNED',
  OVERDUE = 'OVERDUE'
}

// Request DTOs for API communication
export interface CreateLoanRequest {
  readonly bookId: number;
  readonly memberId: number;
}

export interface ReturnLoanRequest {
  readonly loanId: number;
  readonly returnDate: string;
}

// Frontend-specific interfaces for enhanced functionality
export interface LoanWithDetails extends LoanTO {
  readonly daysUntilDue?: number;
  readonly isOverdue?: boolean;
}

// Search and filtering (frontend-only)
export interface LoanSearchCriteria {
  readonly memberId?: number;
  readonly bookId?: number;
  readonly status?: LoanStatus;
  readonly isOverdue?: boolean;
}