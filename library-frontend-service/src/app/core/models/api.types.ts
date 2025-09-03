// Re-export all models for consistent imports
export * from './book.model';
export * from './member.model';
export * from './loan.model';

// Extend backend DTOs with frontend-specific functionality
import { BookTO, MemberTO, LoanTO } from './';

// Enhanced book interface with availability calculation
export interface BookWithAvailability extends BookTO {
  readonly isAvailable: boolean;
  readonly availabilityStatus: 'available' | 'limited' | 'unavailable';
}

// Enhanced member interface with loan statistics
export interface MemberWithStats extends MemberTO {
  readonly activeLoansCount: number;
  readonly overdueLoansCount: number;
  readonly totalBooksLoaned: number;
}

// Enhanced loan interface with calculated fields
export interface LoanWithDetails extends LoanTO {
  readonly isOverdue: boolean;
  readonly daysUntilDue: number;
}

// Dashboard statistics (frontend-only)
export interface DashboardStats {
  readonly totalBooks: number;
  readonly totalMembers: number;
  readonly activeLoans: number;
  readonly overdueLoans: number;
  readonly availableBooks: number;
}

// Search and filtering interfaces (frontend-only)
export interface SearchFilters {
  readonly query?: string;
  readonly author?: string;
  readonly availability?: 'all' | 'available' | 'unavailable';
  readonly sortBy?: 'title' | 'author' | 'availability';
  readonly sortOrder?: 'asc' | 'desc';
}

// Pagination parameters (future enhancement)
export interface PaginationParams {
  readonly page: number;
  readonly size: number;
}

// Paginated response (future enhancement)
export interface PaginatedResponse<T> {
  readonly content: T[];
  readonly totalElements: number;
  readonly totalPages: number;
  readonly currentPage: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
}